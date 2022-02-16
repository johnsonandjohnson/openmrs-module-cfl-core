package org.openmrs.module.cfl.api.htmlformentry.context;

import org.apache.velocity.VelocityContext;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.htmlformentry.model.Interruption;
import org.openmrs.module.cfl.api.htmlformentry.model.Treatment;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreatmentDataContentProvider implements VelocityContextContentProvider {

  private static final String PROGRAM_FORM_ENCOUNTER_TYPE_UUID =
      "b3af129b-90c9-4f84-966e-f4f9515083e6";

  private static final String TREATMENTS_VELOCITY_PROPERTY_NAME = "treatments";

  @Override
  public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
    Optional<Encounter> currentEncounter =
        findCurrentEncounterByPatient(formEntrySession.getPatient());
    if (currentEncounter.isPresent()) {
      List<Obs> treatmentGroups = findTreatmentGroups(currentEncounter.get());
      List<Treatment> treatments = buildAllTreatments(treatmentGroups);

      formEntrySession.addToVelocityContext(TREATMENTS_VELOCITY_PROPERTY_NAME, treatments);
    }
  }

  private Optional<Encounter> findCurrentEncounterByPatient(Patient patient) {
    EncounterService encounterService = Context.getEncounterService();
    EncounterType encounterType =
        encounterService.getEncounterTypeByUuid(PROGRAM_FORM_ENCOUNTER_TYPE_UUID);
    EncounterSearchCriteria searchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setPatient(patient)
            .setEncounterTypes(Collections.singletonList(encounterType))
            .setIncludeVoided(false)
            .createEncounterSearchCriteria();
    List<Encounter> encounters = encounterService.getEncounters(searchCriteria);
    return encounters.stream().max(Comparator.comparing(Encounter::getEncounterId));
  }

  private List<Treatment> buildAllTreatments(List<Obs> treatmentGroups) {
    List<Treatment> treatments = new ArrayList<>();
    for (Obs treatmentGroup : treatmentGroups) {
      Treatment treatment = new Treatment();
      List<Interruption> interruptions = new ArrayList<>();

      for (Obs obs : treatmentGroup.getGroupMembers()) {
        buildTreatment(treatment, obs);
        buildInterruptions(interruptions, obs);
      }
      treatment.setInterruptions(interruptions);
      treatments.add(treatment);
    }
    return treatments;
  }

  private List<Obs> findTreatmentGroups(Encounter currentEncounter) {
    List<Obs> obsList = new ArrayList<>(currentEncounter.getAllObs());
    return obsList.stream()
        .filter(
            obs ->
                obs.getConcept()
                    .equals(
                        Context.getConceptService()
                            .getConceptByUuid(Treatment.TREATMENT_DETAILS_GROUP_CONCEPT_UUID)))
        .collect(Collectors.toList());
  }

  private void buildTreatment(Treatment treatment, Obs treatmentChild) {
    ConceptService conceptService = Context.getConceptService();

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_START_DATE_CONCEPT_UUID))) {
      treatment.setStartDate(treatmentChild.getValueDatetime());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_STOP_DATE_CONCEPT_UUID))) {
      treatment.setStopDate(treatmentChild.getValueDatetime());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_REGIMEN_CONCEPT_UUID))) {
      treatment.setRegimenName(treatmentChild.getValueText());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_REASON_CONCEPT_UUID))) {
      treatment.setReason(treatmentChild.getValueCoded().getName().getName());
    }
  }

  private void buildInterruptions(List<Interruption> interruptions, Obs interruptionParent) {
    if (interruptionParent
        .getConcept()
        .equals(
            Context.getConceptService()
                .getConceptByUuid(Interruption.INTERRUPTION_DETAILS_GROUP_CONCEPT_UUID))) {
      ConceptService conceptService = Context.getConceptService();

      Interruption interruption = new Interruption();
      for (Obs interruptionChild : interruptionParent.getGroupMembers()) {
        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(
                    Interruption.INTERRUPTION_START_DATE_CONCEPT_UUID))) {
          interruption.setStartDate(interruptionChild.getValueDatetime());
        }

        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(Interruption.INTERRUPTION_REASON_CONCEPT_UUID))) {
          interruption.setReason(interruptionChild.getValueText());
        }

        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(
                    Interruption.INTERRUPTION_RESTART_DATE_CONCEPT_UUID))) {
          interruption.setRestartDate(interruptionChild.getValueDatetime());
        }
      }

      interruptions.add(interruption);
    }
  }
}
