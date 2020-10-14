package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
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
        Vaccination[] vaccinations = getConfigService().getRandomizationGlobalProperty();

        Patient patient = previousVisit.getPatient();
        String patientVaccinationProgram = getConfigService().getVaccinationProgram(patient);
        Vaccination vaccination = Vaccination.findByVaccinationProgram(vaccinations, patientVaccinationProgram);

        List<VisitInformation> visits = vaccination.getVisits();
        List<VisitInformation> futureVisitInformation = new ArrayList<VisitInformation>();
        for (int i = 0; i < visits.size(); i++) {
            if (StringUtils.equalsIgnoreCase(visits.get(i).getNameOfDose(), previousVisit.getVisitType().getName())) {
                for (int j = 1; j <= visits.get(i).getNumberOfFutureVisit(); j++) {
                    futureVisitInformation.add(visits.get(i + j));
                }
            }
        }
        return futureVisitInformation;
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
