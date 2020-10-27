package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;

import java.util.Date;
import java.util.List;

public final class VisitUtil {

    private VisitUtil() {
    }

    public static VisitType getProperVisitType(VisitInformation visitInformation) {
        List<VisitType> visitTypes = Context.getVisitService().getAllVisitTypes();
        VisitType visitType = null;
        for (VisitType vt : visitTypes) {
            if (visitType == null && StringUtils.equalsIgnoreCase(vt.getName(), CFLConstants.OTHER_VISIT_TYPE_NAME)) {
                visitType = vt;
            } else if (StringUtils.equalsIgnoreCase(vt.getName(), visitInformation.getNameOfDose())) {
                visitType = vt;
                break;
            }
        }
        return visitType;
    }

    public static Visit createVisitResource(Patient patient, Date startDateTime, VisitInformation visitInformation) {
        Visit visit = new Visit();

        visit.setPatient(patient);
        visit.setStartDatetime(DateUtil.addDaysToDate(startDateTime,
                visitInformation.getMidPointWindow()));
        visit.setVisitType(getProperVisitType(visitInformation));

        visit.setAttribute(createAttribute(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME,
                CFLConstants.SCHEDULED_VISIT_STATUS));

        visit.setAttribute(createAttribute(CFLConstants.UP_WINDOW_ATTRIBUTE_NAME,
                String.valueOf(visitInformation.getUpWindow())));

        visit.setAttribute(createAttribute(CFLConstants.LOW_WINDOW_ATTRIBUTE_NAME,
                String.valueOf(visitInformation.getLowWindow())));

        visit.setAttribute(createAttribute(CFLConstants.DOSE_NUMBER_ATTRIBUTE_NAME,
                String.valueOf(visitInformation.getDoseNumber())));

        return visit;
    }

    public static Visit getLastDosingVisit(Patient patient) {
        Randomization randomization = getConfigService().getRandomizationGlobalProperty();
        String patientVaccinationProgram = getConfigService().getVaccinationProgram(patient);
        Vaccination vaccination = randomization.findByVaccinationProgram(patientVaccinationProgram);

        List<Visit> allPatientVisits = Context.getVisitService().getVisitsByPatient(patient);

        String followUpTypeName = "";
        Visit lastVisit = null;
        for (VisitInformation vi : vaccination.getVisits()) {
            if (isFollowUpVisit(vi)) {
                followUpTypeName = vi.getNameOfDose();
            }
            for (Visit visit : allPatientVisits) {
                if (StringUtils.equalsIgnoreCase(vi.getNameOfDose(), visit.getVisitType().getName())
                        && !StringUtils.equalsIgnoreCase(visit.getVisitType().getName(), followUpTypeName)) {
                    lastVisit = visit;
                    break;
                }
            }
        }
        return lastVisit;
    }

    public static String getVisitStatus(Visit visit) {
        VisitAttributeType visitStatusAttrType =
                getVisitAttributeTypeByName(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME);
        for (VisitAttribute visitAttribute : visit.getActiveAttributes()) {
            if (visitStatusAttrType != null && StringUtils.equalsIgnoreCase(visitAttribute.getAttributeType().getName(),
                    visitStatusAttrType.getName())) {
                return visitAttribute.getValueReference();
            }
        }
        return "";
    }

    public static String getOccurredVisitStatus() {
        return Context.getAdministrationService().getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    }

    private static boolean isFollowUpVisit(VisitInformation visitInformation) {
        return visitInformation.getNumberOfFutureVisit() == 0;
    }

    private static VisitAttributeType getVisitAttributeTypeByName(String name) {
        for (VisitAttributeType visitAttributeType : Context.getVisitService().getAllVisitAttributeTypes()) {
            if (visitAttributeType.getName().toLowerCase().equals(name.toLowerCase())) {
                return visitAttributeType;
            }
        }
        return null;
    }

    private static VisitAttribute createAttribute(String attributeType, String value) {
        VisitAttribute visitAttribute = new VisitAttribute();
        visitAttribute.setAttributeType(
                VisitUtil.getVisitAttributeTypeByName(attributeType));
        visitAttribute.setValueReferenceInternal(value);

        return visitAttribute;
    }

    private static ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
