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
            LOGGER.info("Regimen updated for participant : {} to : {} ", person.getPersonId(), newRegimen);
            Patient patient = Context.getPatientService().getPatientByUuid(person.getUuid());
            List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
            if (null != visits && !visits.isEmpty()) {
                Visit visit = VisitUtil.getLastOccurredDosingVisit(visits);
                Context.getService(VaccinationService.class).rescheduleVisits(visit, patient);
            }
        }
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
