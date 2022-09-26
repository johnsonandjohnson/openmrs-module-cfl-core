/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.contract.VisitInformation;
import org.openmrs.module.cflcore.api.service.ConfigService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.openmrs.module.cflcore.CFLConstants.DOSING_VISIT_TYPE_NAME;

public final class VisitUtil {

  private VisitUtil() {}

  public static VisitType getProperVisitType(VisitInformation visitInformation) {
    List<VisitType> visitTypes = Context.getVisitService().getAllVisitTypes();
    VisitType visitType = null;
    for (VisitType vt : visitTypes) {
      if (visitType == null
          && StringUtils.equalsIgnoreCase(vt.getName(), CFLConstants.OTHER_VISIT_TYPE_NAME)) {
        visitType = vt;
      } else if (StringUtils.equalsIgnoreCase(vt.getName(), visitInformation.getNameOfDose())) {
        visitType = vt;
        break;
      }
    }
    return visitType;
  }

  public static Visit createVisitResource(
      Patient patient, Date startDateTime, VisitInformation visitInformation) {
    Visit visit = new Visit();

    visit.setPatient(patient);
    visit.setStartDatetime(
        DateUtil.addDaysToDate(startDateTime, visitInformation.getMidPointWindow()));
    visit.setVisitType(getProperVisitType(visitInformation));
    visit.setDateChanged(new Date());
    visit.setChangedBy(Context.getAuthenticatedUser());

    setRequiredVisitAttributes(visit, visitInformation);

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

  public static Visit getLastOccurredDosingVisit(List<Visit> visits) {
    Optional<Visit> lastDosingVisit =
        visits.stream()
            .filter(p -> p.getVisitType().getName().equals(DOSING_VISIT_TYPE_NAME))
            .filter(p -> VisitUtil.getVisitStatus(p).equals(VisitUtil.getOccurredVisitStatus()))
            .findFirst();
    return lastDosingVisit.orElse(null);
  }

  public static String getVisitStatus(Visit visit) {
    VisitAttributeType visitStatusAttrType =
        getVisitAttributeTypeByName(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME);
    for (VisitAttribute visitAttribute : visit.getActiveAttributes()) {
      if (visitStatusAttrType != null
          && StringUtils.equalsIgnoreCase(
              visitAttribute.getAttributeType().getName(), visitStatusAttrType.getName())) {
        return visitAttribute.getValueReference();
      }
    }
    return "";
  }

  public static VisitAttribute getDoseNumberAttr(Visit visit) {
    VisitAttributeType doseNumberAttrType =
        getVisitAttributeTypeByName(CFLConstants.DOSE_NUMBER_ATTRIBUTE_NAME);
    VisitAttribute doseNumberAttr = null;
    for (VisitAttribute visitAttribute : visit.getActiveAttributes()) {
      if (doseNumberAttrType != null
          && StringUtils.equalsIgnoreCase(
              visitAttribute.getAttributeType().getName(), doseNumberAttrType.getName())) {
        doseNumberAttr = visitAttribute;
        break;
      }
    }
    return doseNumberAttr;
  }

  public static String getOccurredVisitStatus() {
    return Context.getAdministrationService()
        .getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
  }

  /**
   * Gets number of vaccination doses to which the patient is assigned
   *
   * @param patient the patient for whom we get the number of doses
   * @return the number of doses of vaccine to which the patient is assigned
   * @throws IllegalArgumentException if patient has no vaccination program assigned
   */
  public static int getNumberOfDosesForPatient(Patient patient) {
    Randomization randomization = getCFLConfigService().getRandomizationGlobalProperty();
    Vaccination vaccination =
        randomization.findByVaccinationProgram(
            getCFLConfigService().getVaccinationProgram(patient));
    if (vaccination != null) {
      return vaccination.getNumberOfDose();
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Patient with name: %s and id: %d has no vaccination " + "program assigned",
              PatientUtil.getPatientFullName(patient), patient.getId()));
    }
  }

  /**
   * Checks if the given patient's visit is the last dosing visit
   *
   * @param patient the patient for whom the checking is performed
   * @param visitInformation the visit details
   * @return information if the given visit for given patient is the last dosing visit or not
   */
  public static boolean isLastPatientDosingVisit(
      Patient patient, VisitInformation visitInformation) {
    int numberOfDoses = getNumberOfDosesForPatient(patient);
    return StringUtils.equals(visitInformation.getNameOfDose(), DOSING_VISIT_TYPE_NAME)
        && numberOfDoses == visitInformation.getDoseNumber();
  }

  private static boolean isFollowUpVisit(VisitInformation visitInformation) {
    return visitInformation.getNumberOfFutureVisit() == 0;
  }

  private static VisitAttributeType getVisitAttributeTypeByName(String name) {
    for (VisitAttributeType visitAttributeType :
        Context.getVisitService().getAllVisitAttributeTypes()) {
      if (visitAttributeType.getName().toLowerCase().equals(name.toLowerCase())) {
        return visitAttributeType;
      }
    }
    return null;
  }

  private static VisitAttribute createAttribute(String attributeType, String value) {
    VisitAttribute visitAttribute = new VisitAttribute();
    visitAttribute.setAttributeType(VisitUtil.getVisitAttributeTypeByName(attributeType));
    visitAttribute.setValueReferenceInternal(value);

    return visitAttribute;
  }

  private static void setRequiredVisitAttributes(Visit visit, VisitInformation visitInformation) {
    if (StringUtils.isNotBlank(visitInformation.getVisitTime())) {
      visit.setAttribute(
          createAttribute(
              CFLConstants.VISIT_TIME_ATTRIBUTE_TYPE_NAME, visitInformation.getVisitTime()));
    }

    visit.setAttribute(
        createAttribute(
            CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME, CFLConstants.SCHEDULED_VISIT_STATUS));

    visit.setAttribute(
        createAttribute(
            CFLConstants.UP_WINDOW_ATTRIBUTE_NAME, String.valueOf(visitInformation.getUpWindow())));

    visit.setAttribute(
        createAttribute(
            CFLConstants.LOW_WINDOW_ATTRIBUTE_NAME,
            String.valueOf(visitInformation.getLowWindow())));

    visit.setAttribute(
        createAttribute(
            CFLConstants.DOSE_NUMBER_ATTRIBUTE_NAME,
            String.valueOf(visitInformation.getDoseNumber())));

    visit.setAttribute(
        createAttribute(
            CFLConstants.IS_LAST_DOSING_VISIT_ATTRIBUTE_NAME,
            String.valueOf(isLastPatientDosingVisit(visit.getPatient(), visitInformation))));
  }

  private static ConfigService getCFLConfigService() {
    return Context.getRegisteredComponent(
        CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
  }
}
