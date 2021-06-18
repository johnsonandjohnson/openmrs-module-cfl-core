package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.IrisVisitService;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.Message;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Abstract class for subscribable event listening.
 */
public class RegimenUpdateListener extends PeopleActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegimenUpdateListener.class);

    @Autowired
    private IrisVisitService irisVisitService;

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        Person person = extractPerson(message);
        if (person != null) {
            String newRegimen = getConfigService().getVaccinationProgram(person);
            String oldRegimen = getPreviousRegimen(person.getAttributes());
            LOGGER.info("Regime updated for participant : {} to : {} ", person.getPersonId(), oldRegimen + "::" + newRegimen);
            Patient patient = Context.getPatientService().getPatientByUuid(person.getUuid());
            List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
            if(null != visits && !visits.isEmpty()) {
               Visit visit =  getLastOccurredDosingVisit(visits);
               irisVisitService.voidFutureVisits(patient);
               irisVisitService.createFutureVisits(visit);
            }
            /* first visit information comes from tablet.
            1. If there are no visits in the system then no action
            2. void future visits
            3. Get latest dosing visit with OCCURRED status
            4. Create the remaining visits based on the latest dosing visit and regimen
            */
        }
        }

    private String getPreviousRegimen(Set<PersonAttribute> attributes) {
        Optional<PersonAttribute> attribute = attributes.stream().filter(p -> p.getVoided() &&
                p.getAttributeType().getName().equals("Vaccination program"))
                                                        .min((final PersonAttribute p1, final PersonAttribute p2) -> p2
                                                                .getDateVoided().compareTo(p1.getDateVoided()));

        return attribute.map(PersonAttribute::getValue).orElse(null);
    }

    private Visit getLastOccurredDosingVisit(List<Visit> visits) {

        Optional<Visit> lastDosingVisit = visits.stream().filter(p -> p.getVisitType().getName().equals("Dosing"))
                                                .filter(p -> VisitUtil.getVisitStatus(p)
                                                                      .equals(VisitUtil.getOccurredVisitStatus()))
                                                .findFirst();
        return lastDosingVisit.orElse(null);
    }


    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
