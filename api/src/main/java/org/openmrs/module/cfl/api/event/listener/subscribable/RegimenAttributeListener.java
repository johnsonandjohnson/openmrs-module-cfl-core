package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Message;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.openmrs.module.cfl.CFLConstants.DOSING_VISIT_TYPE_NAME;
import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME;

/**
 * Listener class for Regimen update
 */
public class RegimenAttributeListener extends PersonAttributeActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegimenAttributeListener.class);

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        LOGGER.debug("Regimen Attribute Action listener triggered");
        PersonAttribute personAttribute = extractPersonAttribute(message);
        if (VACCINATION_PROGRAM_ATTRIBUTE_NAME.equals(personAttribute.getAttributeType().getName())) {
            Person person = personAttribute.getPerson();
            String newRegimen = getConfigService().getVaccinationProgram(person);
            LOGGER.info("Regime updated for participant : {} to : {} ", person.getPersonId(), newRegimen);
            Patient patient = Context.getPatientService().getPatientByUuid(person.getUuid());
            List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
            if (null != visits && !visits.isEmpty()) {
                Visit visit = getLastOccurredDosingVisit(visits);
                Context.getService(VaccinationService.class).rescheduleVisits(visit, patient);
            }
        }
    }

    private Visit getLastOccurredDosingVisit(List<Visit> visits) {
        Optional<Visit> lastDosingVisit = visits.stream()
                                                .filter(p -> p.getVisitType().getName().equals(DOSING_VISIT_TYPE_NAME))
                                                .filter(p -> VisitUtil.getVisitStatus(p)
                                                                      .equals(VisitUtil.getOccurredVisitStatus()))
                                                .findFirst();
        return lastDosingVisit.orElse(null);
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
