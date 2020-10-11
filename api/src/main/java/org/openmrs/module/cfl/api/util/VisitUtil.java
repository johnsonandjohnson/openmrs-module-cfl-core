package org.openmrs.module.cfl.api.util;

import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.VisitInformation;

public final class VisitUtil {

    private VisitUtil() {
    }

    public static VisitType getProperVisitType(VisitInformation visitInformation) {
        VisitType visitType = null;
        if (visitInformation.getNameOfDose().equals(CFLConstants.DOSE_1_VISIT_NAME)) {
            visitType = getVisitTypeByUuid(CFLConstants.DOSE_1_VISIT_VISIT_TYPE_UUID);
        } else if (visitInformation.getNameOfDose().equals(CFLConstants.DOSE_1_2_VISIT_NAME)) {
            visitType = getVisitTypeByUuid(CFLConstants.DOSE_1_2_VISIT_VISIT_TYPE_UUID);
        } else if (visitInformation.getNameOfDose().equals(CFLConstants.DOSE_1_2_3_VISIT_NAME)) {
            visitType = getVisitTypeByUuid(CFLConstants.DOSE_1_2_3_VISIT_VISIT_TYPE_UUID);
        } else if (visitInformation.getNameOfDose().equals(CFLConstants.DOSE_1_2_3_4_VISIT_NAME)) {
            visitType = getVisitTypeByUuid(CFLConstants.DOSE_1_2_3_4_VISIT_VISIT_TYPE_UUID);
        } else if (visitInformation.getNameOfDose().equals(CFLConstants.FOLLOW_UP_VISIT_NAME)) {
            visitType = getVisitTypeByUuid(CFLConstants.FOLLOW_UP_VISIT_TYPE_UUID);
        }
        return visitType;
    }

    public static Visit createResourcesForVisit(Visit previousVisit, VisitInformation visitInformation) {
        Visit visit = new Visit();
        visit.setPatient(previousVisit.getPatient());
        visit.setStartDatetime(DateUtil.addDaysToDate(previousVisit.getStartDatetime(),
                visitInformation.getMidPointWindow()));
        visit.setVisitType(getProperVisitType(visitInformation));
        VisitAttributeType visitAttributeType =
                Context.getVisitService().getVisitAttributeTypeByUuid(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
        VisitAttribute visitAttribute = new VisitAttribute();
        visitAttribute.setAttributeType(visitAttributeType);
        visitAttribute.setValueReferenceInternal(CFLConstants.SCHEDULED_VISIT_STATUS);
        visit.setAttribute(visitAttribute);

        return visit;
    }

    private static VisitType getVisitTypeByUuid(String visitTypeUuid) {
        return Context.getVisitService().getVisitTypeByUuid(visitTypeUuid);
    }
}
