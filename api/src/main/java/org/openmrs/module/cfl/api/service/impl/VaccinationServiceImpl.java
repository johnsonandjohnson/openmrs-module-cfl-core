package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VaccinationServiceImpl implements VaccinationService {

    private static final String REGIMEN_CHANGE = "REGIMEN CHANGE";

    @Transactional
    @Override
    public void createFutureVisits(Visit occurredVisit, Date occurrenceDateTime) {
        final Vaccination vaccination = getVaccinationForPatient(occurredVisit.getPatient());
        final int totalNumberOfDoses = vaccination.getNumberOfDose();

        final CountrySetting countrySetting = CountrySettingUtil.
                                                                        getCountrySettingForPatient(
                                                                                occurredVisit.getPatient().getPerson());

        final Visit lastDosingVisit = VisitUtil.getLastDosingVisit(occurredVisit.getPatient(), vaccination);

        // should it create future visits and is this the last dosage visit that this patient had scheduled and it is not
        // the last visit in program
        if (countrySetting.isShouldCreateFutureVisit() && occurredVisit.equals(lastDosingVisit) &&
                !isLastVisit(totalNumberOfDoses, occurredVisit)) {

            final List<VisitInformation> futureVisits = getInformationForFutureVisits(occurredVisit, vaccination);

            for (VisitInformation futureVisit : futureVisits) {
                prepareDataAndSaveVisit(occurredVisit.getPatient(), occurrenceDateTime, futureVisit);
            }
        }
    }

    @Override
    @Transactional
    public void voidFutureVisits(Patient patient) {
        List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);

        for (Visit visit : visits) {
            if (CFLConstants.SCHEDULED_VISIT_STATUS.equals(VisitUtil.getVisitStatus(visit))) {
                Context.getVisitService().voidVisit(visit, REGIMEN_CHANGE);
            }
        }
    }

    @Override
    @Transactional
    public void rescheduleVisits(Visit latestDosingVisit, Patient patient) {
        voidFutureVisits(patient);
        createFutureVisits(latestDosingVisit, latestDosingVisit.getStartDatetime());
    }

    private Vaccination getVaccinationForPatient(Patient patient) {
        final Randomization randomization = getConfigService().getRandomizationGlobalProperty();
        final String patientVaccinationProgram = getConfigService().getVaccinationProgram(patient);
        return randomization.findByVaccinationProgram(patientVaccinationProgram);
    }

    private boolean isLastVisit(int totalNumberOfDoses, Visit occurredVisit) {
        final VisitAttribute visitDoseNumberAttr = VisitUtil.getDoseNumberAttr(occurredVisit);
        return visitDoseNumberAttr != null &&
                totalNumberOfDoses == Integer.parseInt(visitDoseNumberAttr.getValueReference());
    }

    private void prepareDataAndSaveVisit(Patient patient, Date occurrenceDateTime, VisitInformation futureVisit) {
        final Visit visit = VisitUtil.createVisitResource(patient, occurrenceDateTime, futureVisit);

        final PersonAttribute patientLocationAttribute = patient.getPerson().getAttribute(
                CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);

        if (patientLocationAttribute != null) {
            visit.setLocation(Context.getLocationService().getLocationByUuid(patientLocationAttribute.getValue()));
        } else {
            visit.setLocation(patient.getPatientIdentifier().getLocation());
        }

        Context.getVisitService().saveVisit(visit);
    }

    private List<VisitInformation> getInformationForFutureVisits(Visit lastDosingVisit, Vaccination vaccination) {
        final String lastDosingVisitType = lastDosingVisit.getVisitType().getName();
        final List<VisitInformation> visitInformation = vaccination.findByVisitType(lastDosingVisitType);

        if (CollectionUtils.isEmpty(visitInformation)) {
            return new ArrayList<>();
        } else if (visitInformation.size() == 1 && visitInformation.get(0).getNumberOfFutureVisit() == 0) {
            return new ArrayList<>();
        } else if (visitInformation.size() == 1) {
            //We send 1 as numberOfVisits, because in this case we have only one visit with this visitType
            return vaccination.findFutureVisits(lastDosingVisitType, 1);
        } else {
            return vaccination.findFutureVisits(lastDosingVisitType,
                                                getNumberOfVisits(lastDosingVisit.getPatient(), lastDosingVisitType));
        }
    }

    private int getNumberOfVisits(Patient patient, String visitType) {
        return (int) Context.getVisitService().getVisitsByPatient(patient).stream().map(Visit::getVisitType)
                            .map(BaseOpenmrsMetadata::getName)
                            .filter(visitTypeName -> StringUtils.equalsIgnoreCase(visitTypeName, visitType)).count();
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
