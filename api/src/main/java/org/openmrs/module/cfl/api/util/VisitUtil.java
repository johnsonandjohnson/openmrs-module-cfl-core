package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.VisitInformation;

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
}
