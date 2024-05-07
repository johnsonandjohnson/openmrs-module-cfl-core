/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.service;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.LaboratoryDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.PhysicalExaminationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.ProgressNoteDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.ReviewMedicationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.VaccinationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.VitalDetailsDTO;
import java.util.List;
import java.util.Set;

/** Provides methods related to Medical Visit Note functionalities */
public interface MedicalVisitNoteService {

  /**
   * Gets vitals data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link VitalDetailsDTO} objects
   */
  List<VitalDetailsDTO> getVitalData(Encounter encounter);

  /**
   * Gets medication data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link ReviewMedicationDetailsDTO} objects
   */
  List<ReviewMedicationDetailsDTO> getReviewMedicationData(Encounter encounter);

  /**
   * Gets PE data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link PhysicalExaminationDetailsDTO} objects
   */
  List<PhysicalExaminationDetailsDTO> getPhysicalExaminationData(Encounter encounter);

  /**
   * Gets progress notes data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link ProgressNoteDetailsDTO} objects
   */
  List<ProgressNoteDetailsDTO> getProgressNoteData(Encounter encounter);

  /**
   * Gets vaccination data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link VaccinationDetailsDTO} objects
   */
  List<VaccinationDetailsDTO> getVaccinationData(Encounter encounter);

  /**
   * Gets laboratory data from given encounter
   *
   * @param encounter related encounter
   * @return list of {@link LaboratoryDetailsDTO} objects
   */
  List<LaboratoryDetailsDTO> getLaboratoryData(Encounter encounter);

  /**
   * Finds obs with given concept and obs group from set of obs
   *
   * @param obsSet set of observations to search for
   * @param conceptUuid concept uuid
   * @param obsGroup obs group where to search for
   * @return
   */
  Obs findObsByConceptAndGroup(Set<Obs> obsSet, String conceptUuid, Obs obsGroup);
}
