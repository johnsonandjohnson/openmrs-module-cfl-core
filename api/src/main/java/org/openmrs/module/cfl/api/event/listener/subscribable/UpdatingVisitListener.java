package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
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
            Visit visit = extractVisit(message);

            String visitStatus = "";
            Collection<VisitAttribute> activeAttributes = visit.getActiveAttributes();
            for (VisitAttribute visitAttribute : activeAttributes) {
                if (visitAttribute.getAttributeType().getUuid().equals(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID)) {
                    visitStatus = visitAttribute.getValueReference();
                }
            }

            if (visitStatus.equals(Context.getAdministrationService()
                    .getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))) {
                createFutureVisits(visit);
            }
            updateGlobalProperty(getConfigService().getRefreshDate(visit));
        }
    }

    private void updateGlobalProperty(String refreshDate) {
        long lastSaved = Long.parseLong(getConfigService().getLastVisitRefreshDate());
        long visitRefreshDate = Long.parseLong(refreshDate);
        if (visitRefreshDate > lastSaved) {
            getConfigService().setLastVisitRefreshDate(Long.toString(visitRefreshDate));
        }
    }

    private void createFutureVisits(Visit previousVisit) {
        List<VisitInformation> futureVisits = getInformationForFutureVisits(previousVisit);
        for (VisitInformation visitInformation : futureVisits) {
            Visit visit = VisitUtil.createResourcesForVisit(previousVisit, visitInformation);
            Context.getVisitService().saveVisit(visit);
        }
    }

    private List<VisitInformation> getInformationForFutureVisits(Visit previousVisit) {
        Randomization randomization = getConfigService().getRandomizationGlobalProperty();

        Patient patient = previousVisit.getPatient();
        String patientVaccinationProgram = getConfigService().getVaccinationProgram(patient);
        Vaccination vaccination = randomization.findByVaccinationProgram(patientVaccinationProgram);

        String visitType = previousVisit.getVisitType().getName();
        List<VisitInformation> visitInformation = vaccination.findByVisitType(visitType);
        if (CollectionUtils.isEmpty(visitInformation)) {
            return new ArrayList<VisitInformation>();
        } else if (visitInformation.size() == 1 && visitInformation.get(0).getMidPointWindow() == 0) {
            return new ArrayList<VisitInformation>();
        } else if (visitInformation.size() == 1) {
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
