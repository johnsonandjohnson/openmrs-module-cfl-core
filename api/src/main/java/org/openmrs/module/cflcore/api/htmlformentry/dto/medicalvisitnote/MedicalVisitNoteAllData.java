/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote;

import java.util.Date;
import java.util.List;

public class MedicalVisitNoteAllData {

  private String encounterUuid;

  private Date encounterDate;

  private List<VitalDetailsDTO> vitals;

  private List<ReviewMedicationDetailsDTO> medications;

  private List<PhysicalExaminationDetailsDTO> physicalExaminations;

  private List<ProgressNoteDetailsDTO> progressNotes;

  private List<VaccinationDetailsDTO> vaccinations;

  private List<LaboratoryDetailsDTO> laboratoryData;

  public MedicalVisitNoteAllData(
      String encounterUuid,
      Date encounterDate,
      List<VitalDetailsDTO> vitals,
      List<ReviewMedicationDetailsDTO> medications,
      List<PhysicalExaminationDetailsDTO> physicalExaminations,
      List<ProgressNoteDetailsDTO> progressNotes,
      List<VaccinationDetailsDTO> vaccinations,
      List<LaboratoryDetailsDTO> laboratoryData) {
    this.encounterUuid = encounterUuid;
    this.encounterDate = encounterDate;
    this.vitals = vitals;
    this.medications = medications;
    this.physicalExaminations = physicalExaminations;
    this.progressNotes = progressNotes;
    this.vaccinations = vaccinations;
    this.laboratoryData = laboratoryData;
  }

  public String getEncounterUuid() {
    return encounterUuid;
  }

  public void setEncounterUuid(String encounterUuid) {
    this.encounterUuid = encounterUuid;
  }

  public Date getEncounterDate() {
    return encounterDate;
  }

  public void setEncounterDate(Date encounterDate) {
    this.encounterDate = encounterDate;
  }

  public List<VitalDetailsDTO> getVitals() {
    return vitals;
  }

  public void setVitals(List<VitalDetailsDTO> vitals) {
    this.vitals = vitals;
  }

  public List<ReviewMedicationDetailsDTO> getMedications() {
    return medications;
  }

  public void setMedications(List<ReviewMedicationDetailsDTO> medications) {
    this.medications = medications;
  }

  public List<PhysicalExaminationDetailsDTO> getPhysicalExaminations() {
    return physicalExaminations;
  }

  public void setPhysicalExaminations(List<PhysicalExaminationDetailsDTO> physicalExaminations) {
    this.physicalExaminations = physicalExaminations;
  }

  public List<ProgressNoteDetailsDTO> getProgressNotes() {
    return progressNotes;
  }

  public void setProgressNotes(List<ProgressNoteDetailsDTO> progressNotes) {
    this.progressNotes = progressNotes;
  }

  public List<VaccinationDetailsDTO> getVaccinations() {
    return vaccinations;
  }

  public void setVaccinations(List<VaccinationDetailsDTO> vaccinations) {
    this.vaccinations = vaccinations;
  }

  public List<LaboratoryDetailsDTO> getLaboratoryData() {
    return laboratoryData;
  }

  public void setLaboratoryData(List<LaboratoryDetailsDTO> laboratoryData) {
    this.laboratoryData = laboratoryData;
  }
}
