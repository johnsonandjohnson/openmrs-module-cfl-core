/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
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

    public static Visit getLastDosingVisit(Patient patient, Vaccination vaccination) {
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

    public static VisitAttribute getDoseNumberAttr(Visit visit) {
        VisitAttributeType doseNumberAttrType = getVisitAttributeTypeByName(CFLConstants.DOSE_NUMBER_ATTRIBUTE_NAME);
        VisitAttribute doseNumberAttr = null;
        for (VisitAttribute visitAttribute : visit.getActiveAttributes()) {
            if (doseNumberAttrType != null && StringUtils.equalsIgnoreCase(visitAttribute.getAttributeType().getName(),
                    doseNumberAttrType.getName())) {
                doseNumberAttr = visitAttribute;
                break;
            }
        }
        return doseNumberAttr;
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
}
