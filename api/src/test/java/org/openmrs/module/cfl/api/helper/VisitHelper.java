package org.openmrs.module.cfl.api.helper;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;

import java.util.ArrayList;
import java.util.List;

public final class VisitHelper {

    private VisitHelper() {
    }

    public static List<VisitAttributeType> createVisitAttrTypes() {
        List<VisitAttributeType> attributeTypeList = new ArrayList<VisitAttributeType>();
        attributeTypeList.add(createVisitAttrType(1, "Visit Status"));
        attributeTypeList.add(createVisitAttrType(2, "Visit Time"));
        return attributeTypeList;
    }

    public static Visit createVisit(int visitId, Patient patient, String visitType, String visitStatus) {
        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setPatient(patient);
        visit.setVisitType(createVisitType(visitType));
        visit.addAttribute(createVisitStatusAttribute(visitStatus));
        return visit;
    }

    private static VisitType createVisitType(String visitTypeName) {
        VisitType visitType = new VisitType();
        visitType.setId(1);
        visitType.setName(visitTypeName);
        return visitType;
    }

    private static VisitAttribute createVisitStatusAttribute(String visitStatus) {
        VisitAttribute visitAttribute = new VisitAttribute();
        visitAttribute.setId(1);
        visitAttribute.setAttributeType(createVisitStatusAttributeType());
        visitAttribute.setValueReferenceInternal(visitStatus);
        return visitAttribute;
    }

    private static VisitAttributeType createVisitStatusAttributeType() {
        VisitAttributeType visitAttributeType = new VisitAttributeType();
        visitAttributeType.setId(1);
        visitAttributeType.setName("Visit Status");
        return visitAttributeType;
    }

    private static VisitAttributeType createVisitAttrType(int visitAttrTypeId, String attrTypeName) {
        VisitAttributeType visitAttributeType = new VisitAttributeType();
        visitAttributeType.setId(visitAttrTypeId);
        visitAttributeType.setName(attrTypeName);
        return visitAttributeType;
    }
}
