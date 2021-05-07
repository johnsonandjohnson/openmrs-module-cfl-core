package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UpdatingVisitListener extends VisitActionListener {

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        if (getConfigService().isVaccinationInfoIsEnabled()) {
            Visit updatedVisit = extractVisit(message);

            String visitStatus = "";
            Collection<VisitAttribute> activeAttributes = updatedVisit.getActiveAttributes();
            for (VisitAttribute visitAttribute : activeAttributes) {
                if (visitAttribute.getAttributeType().getUuid().equals(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID)) {
                    visitStatus = visitAttribute.getValueReference();
                }
            }

            if (visitStatus.equals(Context.getAdministrationService()
                    .getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))) {
                createFutureVisits(updatedVisit);
            }
        }
    }

    private void createFutureVisits(Visit updatedVisit) {
        Randomization randomization = getConfigService().getRandomizationGlobalProperty();
        String patientVaccinationProgram = getConfigService().getVaccinationProgram(updatedVisit.getPatient());
        Vaccination vaccination = randomization.findByVaccinationProgram(patientVaccinationProgram);
        int numberOfDoses = vaccination.getNumberOfDose();

        Visit lastDosingVisit = VisitUtil.getLastDosingVisit(updatedVisit.getPatient(), vaccination);

        if (StringUtils.equalsIgnoreCase(VisitUtil.getVisitStatus(lastDosingVisit),
                VisitUtil.getOccurredVisitStatus()) && !isLastVisit(numberOfDoses, lastDosingVisit, updatedVisit)) {
            List<VisitInformation> futureVisits = getInformationForFutureVisits(lastDosingVisit, vaccination);
            CountrySetting countrySetting = CountrySettingUtil.
                    getCountrySettingForPatient(updatedVisit.getPatient().getPerson());
            if (countrySetting.isShouldCreateFutureVisit()) {
                for (VisitInformation futureVisit : futureVisits) {
                    prepareDataAndSaveVisit(lastDosingVisit, futureVisit);
                }
            }

        }
    }

    private boolean isLastVisit(int numberOfDoses, Visit lastDosingVisit, Visit updatedVisit) {
        VisitAttribute visitDoseNumberAttr = VisitUtil.getDoseNumberAttr(updatedVisit);
        return visitDoseNumberAttr != null && numberOfDoses ==
                Integer.parseInt(visitDoseNumberAttr.getValueReference()) &&
                !StringUtils.equals(lastDosingVisit.getVisitType().getName(), updatedVisit.getVisitType().getName());
    }

    private void prepareDataAndSaveVisit(Visit updatedVisit, VisitInformation futureVisit) {
        Visit visit = VisitUtil.createVisitResource(updatedVisit.getPatient(),
                updatedVisit.getStartDatetime(), futureVisit);
        PersonAttribute patientLocationAttribute = updatedVisit.getPatient()
                .getPerson().getAttribute(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);
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
