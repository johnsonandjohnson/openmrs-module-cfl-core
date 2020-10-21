package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.VisitInformation;

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
}
