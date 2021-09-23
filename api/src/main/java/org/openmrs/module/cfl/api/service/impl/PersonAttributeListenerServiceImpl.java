package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.PersonAttributeListenerService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME;

/**
 * Default implementation of {@link PersonAttributeListenerService}.
 *
 * @see org.openmrs.module.cfl.api.event.listener.subscribable.PersonAttributeActionListener
 */
public class PersonAttributeListenerServiceImpl implements PersonAttributeListenerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonAttributeListenerServiceImpl.class);

    private ConfigService configService;
    private VisitService visitService;
    private VaccinationService vaccinationService;
    private PatientDAO patientDAO;

    @Override
    @Transactional
    public void onPersonAttributeEvent(Event.Action action, PersonAttribute personAttribute) {
        final Patient patient = patientDAO.getPatientByUuid(personAttribute.getPerson().getUuid());

        updatePatientDateChanged(patient);

        if (VACCINATION_PROGRAM_ATTRIBUTE_NAME.equals(personAttribute.getAttributeType().getName())) {
            boolean isFirstVaccinationAttribute = isFirstVaccinationAttribute(patient);

            if ((action == Event.Action.CREATED && isFirstVaccinationAttribute) ||
                    (action == Event.Action.UPDATED && !isFirstVaccinationAttribute)) {
                updateVaccination(patient);
            }
        }
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setVaccinationService(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    private void updatePatientDateChanged(Patient patient) {
        if (patient != null) {
            patient.setDateChanged(new Date());
            patient.setChangedBy(Context.getUserContext().getAuthenticatedUser());
            patientDAO.savePatient(patient);
        }
    }

    private boolean isFirstVaccinationAttribute(Person person) {
        return person.getAttributes()
                .stream()
                .filter(attr -> attr.getAttributeType().getName().equals(VACCINATION_PROGRAM_ATTRIBUTE_NAME))
                .count() == 1;
    }

    private void updateVaccination(Patient patient) {
        final String newRegimen = configService.getVaccinationProgram(patient.getPerson());

        if (StringUtils.isBlank(newRegimen)) {
            LOGGER.info("Regimen removed for participant : {}", patient.getPerson().getPersonId());

            vaccinationService.voidFutureVisits(patient);
        } else {
            LOGGER.info("Regimen updated for participant : {} to : {} ", patient.getPerson().getPersonId(), newRegimen);

            final List<Visit> visits = visitService.getActiveVisitsByPatient(patient);
            final Visit lastOccurredDosingVisit = VisitUtil.getLastOccurredDosingVisit(visits);

            if (lastOccurredDosingVisit != null) {
                vaccinationService.rescheduleVisits(lastOccurredDosingVisit, patient);
            } else if (visits.size() > 0) {
                vaccinationService.voidFutureVisits(patient);
            }
        }
    }
}
