/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.helper;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class VisitHelper {

    private VisitHelper() {
    }

    public static List<VisitAttributeType> createVisitAttrTypes() {
        List<VisitAttributeType> attributeTypeList = new ArrayList<VisitAttributeType>();
        attributeTypeList.add(createVisitAttrType(1, "Visit Status"));
        attributeTypeList.add(createVisitAttrType(2, "Visit Time"));
        attributeTypeList.add(createVisitAttrType(3, "Up Window"));
        attributeTypeList.add(createVisitAttrType(4, "Low Window"));
        attributeTypeList.add(createVisitAttrType(5, "Dose Number"));
        return attributeTypeList;
    }

    public static Visit createVisit(int visitId, Patient patient, String visitType, String visitStatus) {
        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setPatient(patient);
        visit.setVisitType(createVisitType(visitType));
        visit.addAttribute(createVisitStatusAttribute(visitStatus));
        visit.setStartDatetime(new Date());
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
        visitAttributeType.setUuid(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
        return visitAttributeType;
    }

    public static VisitAttribute createVisitAttribute(int id, String valueReference, String attrTypeName) {
        VisitAttribute visitAttribute = new VisitAttribute();
        visitAttribute.setId(id);
        visitAttribute.setAttributeType(createVisitAttrType(id, attrTypeName));
        visitAttribute.setValueReferenceInternal(valueReference);
        return visitAttribute;
    }

    private static VisitAttributeType createVisitAttrType(int visitAttrTypeId, String attrTypeName) {
        VisitAttributeType visitAttributeType = new VisitAttributeType();
        visitAttributeType.setId(visitAttrTypeId);
        visitAttributeType.setName(attrTypeName);
        return visitAttributeType;
    }

    public static List<VisitType> getVisitTypes() {
        List<VisitType> visitTypes = new ArrayList<VisitType>();
        VisitType visitTypeDosing = new VisitType();
        VisitType visitTypeOther = new VisitType();
        visitTypeDosing.setName(Constant.VISIT_TYPE_DOSING);
        visitTypeOther.setName(Constant.VISIT_TYPE_OTHER);
        visitTypes.add(visitTypeDosing);
        return visitTypes;
    }

    public static List<VisitAttributeType> getVisitAttributeTypes() {
        List<VisitAttributeType> visitAttributeTypes = new ArrayList<VisitAttributeType>();
        VisitAttributeType visitAttributeType1 = new VisitAttributeType();
        VisitAttributeType visitAttributeType2 = new VisitAttributeType();
        VisitAttributeType visitAttributeType3 = new VisitAttributeType();
        VisitAttributeType visitAttributeType4 = new VisitAttributeType();
        visitAttributeType1.setName(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME);
        visitAttributeType2.setName(CFLConstants.UP_WINDOW_ATTRIBUTE_NAME);
        visitAttributeType3.setName(CFLConstants.LOW_WINDOW_ATTRIBUTE_NAME);
        visitAttributeType4.setName(CFLConstants.DOSE_NUMBER_ATTRIBUTE_NAME);
        visitAttributeTypes.add(visitAttributeType1);
        visitAttributeTypes.add(visitAttributeType2);
        visitAttributeTypes.add(visitAttributeType3);
        visitAttributeTypes.add(visitAttributeType4);
        return visitAttributeTypes;
    }

    public static List<Visit> getVisits(Visit visit) {
        List<Visit> visits = new ArrayList<Visit>();
        visits.add(visit);
        return visits;
    }
}
