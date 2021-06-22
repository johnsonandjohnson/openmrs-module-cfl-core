/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.IrisVisitService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

public class IrisVisitServiceImpl extends BaseOpenmrsService implements IrisVisitService {

    private static final String REGIMEN_CHANGE = "REGIMEN CHANGE";
    private VisitService visitService;

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public Visit saveVisit(Visit visit) {
        return visitService.saveVisit(visit);
    }

    @Override
    @Transactional
    public void voidFutureVisits(Patient patient) {
        List<Visit> visits = visitService.getActiveVisitsByPatient(patient);

        for (Visit visit : visits) {
            if (CFLConstants.SCHEDULED_VISIT_STATUS.equals(VisitUtil.getVisitStatus(visit))){
                visitService.voidVisit(visit, REGIMEN_CHANGE);
            }
        }
    }

    @Override
    @Transactional
    public void createFutureVisits(Visit updatedVisit) {
        //cfl.vaccines json
        Randomization randomization = getConfigService().getRandomizationGlobalProperty();
        // value of vaccination attribute stored in person attribute
        String patientVaccinationProgram = getConfigService().getVaccinationProgram(updatedVisit.getPatient());
        // Get json configuration for regimen
        Vaccination vaccination = randomization.findByVaccinationProgram(patientVaccinationProgram);
        int numberOfDoses = vaccination.getNumberOfDose();

        Visit lastDosingVisit = VisitUtil.getLastDosingVisit(updatedVisit.getPatient(), vaccination);

        // if status is occurred and not last dosing visit (check dosing number attribute)
        if (StringUtils
                .equalsIgnoreCase(VisitUtil.getVisitStatus(lastDosingVisit), VisitUtil.getOccurredVisitStatus()) &&
                !isLastVisit(numberOfDoses, lastDosingVisit, updatedVisit)) {
            List<VisitInformation> futureVisits = getInformationForFutureVisits(lastDosingVisit, vaccination);
            CountrySetting countrySetting = CountrySettingUtil.
                                                                      getCountrySettingForPatient(
                                                                              updatedVisit.getPatient().getPerson());
            if (countrySetting.isShouldCreateFutureVisit()) {
                for (VisitInformation futureVisit : futureVisits) {
                    prepareDataAndSaveVisit(lastDosingVisit, futureVisit);
                }
            }

        }
    }

    @Override
    @Transactional
    public void updateVisitsForRegimenChange(Visit latestDosingVisit, Patient patient) {
        voidFutureVisits(patient);
        createFutureVisits(latestDosingVisit);
    }

    private boolean isLastVisit(int numberOfDoses, Visit lastDosingVisit, Visit updatedVisit) {
        //each visit stores dose number as attribute
        VisitAttribute visitDoseNumberAttr = VisitUtil.getDoseNumberAttr(updatedVisit);
        return visitDoseNumberAttr != null &&
                numberOfDoses == Integer.parseInt(visitDoseNumberAttr.getValueReference()) &&
                !StringUtils.equals(lastDosingVisit.getVisitType().getName(), updatedVisit.getVisitType().getName());
    }

    private void prepareDataAndSaveVisit(Visit updatedVisit, VisitInformation futureVisit) {
        Visit visit = VisitUtil
                .createVisitResource(updatedVisit.getPatient(), updatedVisit.getStartDatetime(), futureVisit);
        PersonAttribute patientLocationAttribute = updatedVisit.getPatient().getPerson().getAttribute(
                CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);
        if (null != patientLocationAttribute) {
            visit.setLocation(Context.getLocationService().getLocationByUuid(patientLocationAttribute.getValue()));
        } else {
            visit.setLocation(updatedVisit.getPatient().getPatientIdentifier().getLocation());
        }
        Context.getVisitService().saveVisit(visit);
    }

    private List<VisitInformation> getInformationForFutureVisits(Visit updatedVisit, Vaccination vaccination) {
        Patient patient = updatedVisit.getPatient();

        String visitType = updatedVisit.getVisitType().getName();
        List<VisitInformation> visitInformation = vaccination.findByVisitType(visitType);

        if (CollectionUtils.isEmpty(visitInformation)) {
            return new ArrayList<VisitInformation>();
        } else if (visitInformation.size() == 1 && visitInformation.get(0).getNumberOfFutureVisit() == 0) {
            return new ArrayList<VisitInformation>();
        } else if (visitInformation.size() == 1) {
            //We send 1 as numberOfVisits, because in this case we have only one visit with this visitType
            return vaccination.findFutureVisits(visitType, 1);
        } else {
            return vaccination.findFutureVisits(visitType, getNumberOfVisits(patient, visitType));
        }
    }

    private int getNumberOfVisits(Patient patient, String visitType) {
        List<Visit> allVisitsForPatient = Context.getVisitService().getVisitsByPatient(patient);
        List<Visit> visitsForPatientByType = new ArrayList<Visit>();
        for (Visit visit : allVisitsForPatient) {
            if (StringUtils.equalsIgnoreCase(visit.getVisitType().getName(), visitType)) {
                visitsForPatientByType.add(visit);
            }
        }
        return visitsForPatientByType.size();
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }

}
