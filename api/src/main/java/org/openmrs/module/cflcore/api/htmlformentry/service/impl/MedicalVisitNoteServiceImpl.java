/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.LaboratoryDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.PhysicalExaminationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.ProgressNoteDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.ReviewMedicationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.VaccinationDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.VitalDetailsDTO;
import org.openmrs.module.cflcore.api.htmlformentry.model.LaboratoryDataModel;
import org.openmrs.module.cflcore.api.htmlformentry.model.PhysicalExaminationDataModel;
import org.openmrs.module.cflcore.api.htmlformentry.model.VaccinationDataModel;
import org.openmrs.module.cflcore.api.htmlformentry.util.LaboratoryUtil;
import org.openmrs.module.cflcore.api.htmlformentry.util.PhysicalExaminationUtil;
import org.openmrs.module.cflcore.api.htmlformentry.util.VaccinationUtil;
import org.openmrs.module.cflcore.api.htmlformentry.service.MedicalVisitNoteService;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.cflcore.api.htmlformentry.context.HtmlFormContextUtil.getObsStringValue;

@Transactional
public class MedicalVisitNoteServiceImpl implements MedicalVisitNoteService {

  @Override
  public List<VitalDetailsDTO> getVitalData(Encounter encounter) {

    Set<Obs> encounterObsList = encounter.getAllObs(false);
    List<VitalDetailsDTO> vitalDetailDTOS = new ArrayList<>();
    for (Obs obs : encounterObsList) {
      String conceptUuid = obs.getConcept().getUuid();
      if (!VitalDetailsDTO.VITALS_HISTORY_CONCEPT_UUID_LIST.contains(conceptUuid)) {
        continue;
      }
      vitalDetailDTOS.add(new VitalDetailsDTO(conceptUuid, getObsStringValue(obs)));
    }

    return vitalDetailDTOS;
  }

  @Override
  public List<ReviewMedicationDetailsDTO> getReviewMedicationData(Encounter encounter) {

    Set<Obs> encounterObsList = encounter.getAllObs(false);
    List<ReviewMedicationDetailsDTO> reviewMedicationDetailDTOS = new ArrayList<>();
    for (Obs obs : encounterObsList) {
      if (!ReviewMedicationDetailsDTO.MEDICATION_CONCEPT_UUID.equals(obs.getConcept().getUuid())) {
        continue;
      }
      Obs medicationStartDateObs =
          findObsByConceptAndGroup(
              encounterObsList,
              ReviewMedicationDetailsDTO.MEDICATION_START_DATE_CONCEPT_UUID,
              obs.getObsGroup());
      Obs medicationEndDateObs =
          findObsByConceptAndGroup(
              encounterObsList,
              ReviewMedicationDetailsDTO.MEDICATION_END_DATE_CONCEPT_UUID,
              obs.getObsGroup());
      reviewMedicationDetailDTOS.add(
          new ReviewMedicationDetailsDTO(
              getObsStringValue(obs),
              getObsStringValue(medicationStartDateObs),
              getObsStringValue(medicationEndDateObs)));
    }

    return reviewMedicationDetailDTOS;
  }

  @Override
  public List<PhysicalExaminationDetailsDTO> getPhysicalExaminationData(Encounter encounter) {
    Set<Obs> encounterObsList = encounter.getAllObs(false);
    List<PhysicalExaminationDetailsDTO> peDetailsDTOS = new ArrayList<>();
    for (Obs obs : encounterObsList) {
      if (!PhysicalExaminationDetailsDTO.PHYSICAL_EXAMINATION_CONCEPT_UUID.equals(
          obs.getConcept().getUuid())) {
        continue;
      }
      Concept specificPEConcept = obs.getValueCoded();
      PhysicalExaminationDataModel peDataModel =
          PhysicalExaminationUtil.findPhysicalExaminationDataModelByPENameConceptUuid(
              specificPEConcept.getUuid());
      Obs peResult = findObsByConcept(encounterObsList, peDataModel.getResultConceptUuid());
      Obs peDetails = findObsByConcept(encounterObsList, peDataModel.getDetailsConceptUuid());

      peDetailsDTOS.add(
          new PhysicalExaminationDetailsDTO(
              getObsStringValue(obs), getObsStringValue(peResult), getObsStringValue(peDetails)));
    }

    return peDetailsDTOS;
  }

  @Override
  public List<ProgressNoteDetailsDTO> getProgressNoteData(Encounter encounter) {
    List<ProgressNoteDetailsDTO> progressNoteDetailDTOS = new ArrayList<>();
    Set<Obs> encounterObsList = encounter.getAllObs(false);
    encounterObsList.stream()
        .filter(
            obs ->
                ProgressNoteDetailsDTO.PROGRESS_NOTE_CONCEPT_UUID.equals(
                    obs.getConcept().getUuid()))
        .findFirst()
        .ifPresent(
            progressNoteObs ->
                progressNoteDetailDTOS.add(
                    new ProgressNoteDetailsDTO(getObsStringValue(progressNoteObs))));

    return progressNoteDetailDTOS;
  }

  @Override
  public List<VaccinationDetailsDTO> getVaccinationData(Encounter encounter) {
    Set<Obs> encounterObsList = encounter.getAllObs(false);
    List<VaccinationDetailsDTO> vaccinationDetailDTOS = new ArrayList<>();
    for (Obs obs : encounterObsList) {
      if (!VaccinationDetailsDTO.VACCINATION_CONCEPT_UUID.equals(obs.getConcept().getUuid())) {
        continue;
      }
      Concept specificVaccinationConcept = obs.getValueCoded();
      VaccinationDataModel vaccinationDataModel =
          VaccinationUtil.findVaccinationDataModelByVaccinationUuid(
              specificVaccinationConcept.getUuid());

      encounterObsList.stream()
          .filter(
              encounterObs ->
                  StringUtils.equals(
                      encounterObs.getConcept().getUuid(),
                      vaccinationDataModel.getVaccinationDateReceivedConceptUuid()))
          .forEach(
              dateReceivedObs ->
                  vaccinationDetailDTOS.add(
                      new VaccinationDetailsDTO(
                          getObsStringValue(obs),
                          getObsStringValue(dateReceivedObs),
                          getObsStringValue(
                              findObsByConceptAndGroup(
                                  encounterObsList,
                                  vaccinationDataModel.getVaccinationLotNumberConceptUuid(),
                                  dateReceivedObs.getObsGroup())))));
    }

    return vaccinationDetailDTOS;
  }

  @Override
  public List<LaboratoryDetailsDTO> getLaboratoryData(Encounter encounter) {
    Set<Obs> encounterObsList = encounter.getAllObs(false);
    List<LaboratoryDetailsDTO> laboratoryDetails = new ArrayList<>();
    for (Obs obs : encounterObsList) {
      if (!LaboratoryUtil.CONCEPT_DATE_DONE_UUID_LIST.contains(obs.getConcept().getUuid())) {
        continue;
      }
      LaboratoryDataModel laboratoryDataModel =
          LaboratoryUtil.findLaboratoryDataModelByDateDoneConceptUuid(obs.getConcept().getUuid());
      Obs studyResultObs =
          findObsByConcept(encounterObsList, laboratoryDataModel.getResultsConceptUuid());
      Obs symbolObs =
          findObsByConcept(encounterObsList, laboratoryDataModel.getSymbolConceptUuid());
      Obs clearanceObs =
          findObsByConcept(encounterObsList, laboratoryDataModel.getClearanceConceptUuid());
      laboratoryDetails.add(
          new LaboratoryDetailsDTO(
              laboratoryDataModel.getStudyName(),
              getObsStringValue(obs),
              getObsStringValue(studyResultObs),
              getObsStringValue(symbolObs),
              laboratoryDataModel.getResultUnitLabel(),
              getObsStringValue(clearanceObs)));
    }

    return laboratoryDetails;
  }

  private Obs findObsByConceptAndGroup(Set<Obs> obsSet, String conceptUuid, Obs obsGroup) {
    return obsSet.stream()
        .filter(obs -> obsGroup.equals(obs.getObsGroup()))
        .filter(obs -> conceptUuid.equals(obs.getConcept().getUuid()))
        .findFirst()
        .orElse(null);
  }

  private Obs findObsByConcept(Set<Obs> obsSet, String conceptUuid) {
    if (conceptUuid == null) {
      return null;
    }

    return obsSet.stream()
        .filter(obs -> StringUtils.equals(obs.getConcept().getUuid(), conceptUuid))
        .findFirst()
        .orElse(null);
  }
}
