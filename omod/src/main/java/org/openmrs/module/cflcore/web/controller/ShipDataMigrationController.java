package org.openmrs.module.cflcore.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("cfl.shipDataMigration")
@RequestMapping("/cfl/ship-data-migration")
public class ShipDataMigrationController {

  /**
   * This API adds data to database (Obses) required after changes in Medicine Refill Visit form
   * (AGRE-3033). It should be executed only once after applying changes in form.
   */
  @RequestMapping(value = "/refill-visit-note", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> migrateRefillVisitNoteData() {
    List<Encounter> allRefillVisitNoteEncounters = findRefillVisitNoteEncounters();

    for (Encounter refillEncounter : allRefillVisitNoteEncounters) {
      Optional<Obs> medicationDetailsObs =
          refillEncounter.getAllObs(false).stream()
              .filter(
                  obs ->
                      StringUtils.equalsIgnoreCase(
                          obs.getConcept().getUuid(), "30d09891-6bae-492c-b45b-ebe142837497"))
              .findFirst();
      medicationDetailsObs.ifPresent(
          obs -> createRequiredObses(obs, allRefillVisitNoteEncounters, refillEncounter));

      Context.getEncounterService().saveEncounter(refillEncounter);
    }

    return new ResponseEntity<>("Data migration completed", HttpStatus.OK);
  }

  @RequestMapping(value = "/medical-visit-note", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> migrateMedicalVisitNoteData() {
    ConceptService conceptService = Context.getConceptService();
    Concept mentalHealthSpecifyConcept =
        conceptService.getConceptByUuid("1638aad5-4fd3-4b5c-ac68-6588c6c586a3");
    Concept mentalHealthMedicalHistoryConcept =
        conceptService.getConceptByUuid("62f00afd-e23f-458d-b7d4-049ed567c008");

    EncounterType medicalVisitEncounterType =
        Context.getEncounterService()
            .getEncounterTypeByUuid("968789ca-e9c4-492f-b1dc-101fd1aa2026");
    ObsService obsService = Context.getObsService();
    List<Obs> mentalHealthSpecifyObsList =
        obsService
            .getObservations(
                null,
                null,
                Collections.singletonList(mentalHealthSpecifyConcept),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false)
            .stream()
            .filter(obs -> obs.getEncounter().getEncounterType().equals(medicalVisitEncounterType))
            .collect(Collectors.toList());

    List<Obs> mentalHealthMedicalHistoryObsList =
        obsService
            .getObservations(
                null,
                null,
                Collections.singletonList(mentalHealthMedicalHistoryConcept),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false)
            .stream()
            .filter(obs -> obs.getEncounter().getEncounterType().equals(medicalVisitEncounterType))
            .collect(Collectors.toList());

    for (Obs obs : mentalHealthSpecifyObsList) {
      Encounter encounter = obs.getEncounter();
      Obs mentalHealthGroupDetailsObs = createGroupObs(obs, "d88ecc57-dc35-46c0-bcf4-0a1aefa803f6");
      mentalHealthGroupDetailsObs.addGroupMember(obs);

      Obs relatedMedicalHistoryObs =
          findObsByEncounter(mentalHealthMedicalHistoryObsList, encounter);
      if (relatedMedicalHistoryObs != null) {
        mentalHealthGroupDetailsObs.addGroupMember(relatedMedicalHistoryObs);
        mentalHealthMedicalHistoryObsList.remove(relatedMedicalHistoryObs);
      }

      obsService.saveObs(mentalHealthGroupDetailsObs, "Medical Visit Note Migration");
    }

    for (Obs obs : mentalHealthMedicalHistoryObsList) {
      Obs mentalHealthGroupDetailsObs = createGroupObs(obs, "d88ecc57-dc35-46c0-bcf4-0a1aefa803f6");
      mentalHealthGroupDetailsObs.addGroupMember(obs);

      obsService.saveObs(mentalHealthGroupDetailsObs, "Medical Visit Note Migration");
    }

    return new ResponseEntity<>("Data migration completed", HttpStatus.OK);
  }

  private Obs findObsByEncounter(List<Obs> obsList, Encounter encounter) {
    return obsList.stream()
        .filter(obs -> obs.getEncounter().getEncounterId().equals(encounter.getEncounterId()))
        .findFirst()
        .orElse(null);
  }

  private List<Encounter> findRefillVisitNoteEncounters() {
    EncounterService encounterService = Context.getEncounterService();
    List<EncounterType> refillEncounterType =
        Collections.singletonList(
            encounterService.getEncounterTypeByUuid("bce100cf-db7f-496a-968f-e0f686397912"));
    EncounterSearchCriteria encounterSearchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setEncounterTypes(refillEncounterType)
            .setIncludeVoided(false)
            .createEncounterSearchCriteria();

    return Context.getEncounterService().getEncounters(encounterSearchCriteria);
  }

  @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
  private void createRequiredObses(
      Obs medicationDetailsObs,
      List<Encounter> allRefillVisitNoteEncounters,
      Encounter currentEncounter) {

    String valueText = medicationDetailsObs.getValueText();
    if (StringUtils.isNotBlank(valueText)) {
      String[] medications = valueText.split(";");
      for (String medication : medications) {
        Obs pillDetailsGroupObs =
            createGroupObs(medicationDetailsObs, "6db838eb-55a6-44d6-8554-60dcafd0a620");
        Obs pillsLeftObs = createPillsLeftObs(medicationDetailsObs, medication);
        if (pillsLeftObs.getValueNumeric() != null) {
          pillDetailsGroupObs.addGroupMember(pillsLeftObs);
        }

        Optional<Encounter> previousRefillVisitEncounter =
            allRefillVisitNoteEncounters.stream()
                .filter(enc -> enc.getPatient().equals(currentEncounter.getPatient()))
                .filter(
                    enc ->
                        enc.getEncounterDatetime().before(currentEncounter.getEncounterDatetime()))
                .max(Comparator.comparing(Encounter::getEncounterDatetime));
        if (previousRefillVisitEncounter.isPresent()) {
          String drugName = StringUtils.substringBeforeLast(medication, "_");
          Obs adherenceObs = createAdherenceObs(medicationDetailsObs);
          if (pillsLeftObs.getValueNumeric() != null) {
            setAdherenceValue(
                adherenceObs,
                currentEncounter,
                previousRefillVisitEncounter.get(),
                drugName,
                pillsLeftObs.getValueNumeric());

            if (adherenceObs.getValueNumeric() != null) {
              pillDetailsGroupObs.addGroupMember(adherenceObs);
            }
          }

          if (pillDetailsGroupObs.hasGroupMembers(false)) {
            currentEncounter.addObs(pillDetailsGroupObs);
          }
        }
      }
    }
  }

  private Obs createGroupObs(Obs basicObs, String groupConceptUuid) {
    Obs pillDetailsGroupObs = creaNewObs(basicObs);
    Concept pillDetailsGroupConcept =
        Context.getConceptService().getConceptByUuid(groupConceptUuid);
    pillDetailsGroupObs.setConcept(pillDetailsGroupConcept);

    return pillDetailsGroupObs;
  }

  private Obs createPillsLeftObs(Obs basicObs, String medication) {
    Obs pillsLeftObs = creaNewObs(basicObs);
    Concept pillsLeftConcept =
        Context.getConceptService().getConceptByUuid("7f1693a5-4223-4a75-9eb7-8d547e6ec677");
    double pillsLeft = Double.parseDouble(StringUtils.substringAfterLast(medication, "_"));
    pillsLeftObs.setConcept(pillsLeftConcept);
    pillsLeftObs.setValueNumeric(pillsLeft);

    return pillsLeftObs;
  }

  private Obs createAdherenceObs(Obs basicObs) {
    Obs adherenceObs = creaNewObs(basicObs);
    Concept adherenceScoreConcept =
        Context.getConceptService().getConceptByUuid("ff16180b-db7e-4423-a925-b29c1b1f2c0f");
    adherenceObs.setConcept(adherenceScoreConcept);

    return adherenceObs;
  }

  private void setAdherenceValue(
      Obs adherenceObs,
      Encounter currentEncounter,
      Encounter previousRefillVisitEncounter,
      String drugName,
      double pillsLeft) {
    Set<Order> orders = previousRefillVisitEncounter.getOrders();
    Set<DrugOrder> drugOrders =
        orders.stream()
            .map(order -> (DrugOrder) HibernateUtil.getRealObjectFromProxy(order))
            .collect(Collectors.toSet());
    Optional<DrugOrder> searchedDrugOrder =
        drugOrders.stream()
            .filter(
                drugOrder -> StringUtils.equalsIgnoreCase(drugOrder.getDrug().getName(), drugName))
            .findFirst();
    if (searchedDrugOrder.isPresent()) {
      DrugOrder drugOrder = searchedDrugOrder.get();
      LocalDate currentEncounterDate =
          currentEncounter
              .getEncounterDatetime()
              .toInstant()
              .atZone(ZoneId.systemDefault())
              .toLocalDate();
      LocalDate previousEncounterDate =
          previousRefillVisitEncounter
              .getEncounterDatetime()
              .toInstant()
              .atZone(ZoneId.systemDefault())
              .toLocalDate();
      long daysBetweenVisits = ChronoUnit.DAYS.between(previousEncounterDate, currentEncounterDate);
      double dailyDose = drugOrder.getDose() * drugOrder.getFrequency().getFrequencyPerDay();
      double pillsShouldBeTaken = daysBetweenVisits > 0 ? daysBetweenVisits * dailyDose : dailyDose;
      double oldPillsCount = drugOrder.getQuantity();
      double adherenceScore = ((oldPillsCount - pillsLeft) / pillsShouldBeTaken) * 100;
      adherenceObs.setValueNumeric(adherenceScore);
    }
  }

  private Obs creaNewObs(Obs basicObs) {
    Obs obs = new Obs();
    obs.setPerson(basicObs.getPerson());
    obs.setEncounter(basicObs.getEncounter());
    obs.setObsDatetime(basicObs.getObsDatetime());

    return obs;
  }
}
