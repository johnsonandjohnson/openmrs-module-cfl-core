package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.CFLVisitService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.PatientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CFLVisitServiceImpl implements CFLVisitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFLVisitServiceImpl.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void rescheduleVisitsByPatients(List<Patient> patients) {
        for (Patient patient : patients) {
            try {
                //flushing and clearing session cache due performance issues
                Context.flushSession();
                Context.clearSession();
                getVaccinationService().rescheduleRegimenVisitsByPatient(patient);
            } catch (Exception ex) {
                LOGGER.error(String.format("Error occurred during rescheduling visits for patient with name: " +
                        "%s and id: %d", PatientUtil.getPatientFullName(patient), patient.getId()), ex);
            }
        }
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}
