package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EncountersWidgetFragmentController {

    public static final String PATIENT_ID = "patientId";
    public static final String ENCOUNTERS_ATTR_NAME = "encounters";
    public static final String ENCOUNTER_TYPE_UUID = "encounterTypeUuid";
    public static final Integer DEFAULT_MAX_RECORDS = 10;
    public static final String MAX_RECORDS_ATTR_NAME = "maxRecords";
    public static final String SYMPTOMS_ANSWER_CONCEPT_CLASS_UUID = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";
    public static final String TEXTS_LIST_ATTR_NAME = "textsList";
    public static final String ENCOUNTERS_BOOL_LIST_ATTR_NAME = "encountersBoolList";
    public static final String ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID = "a3ac4721-1035-4480-8fc0-f0407a7ff3f1";

    public void controller(FragmentConfiguration config, FragmentModel model, @RequestParam(PATIENT_ID) Patient patient,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("conceptService") ConceptService conceptService) {

        String encounterTypesUuids = config.get(ENCOUNTER_TYPE_UUID).toString();
        Concept encounterClosedStatusConcept = conceptService.getConceptByUuid(ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID);

        ConceptClass symptomsAnswerConceptClass = conceptService.getConceptClassByUuid(SYMPTOMS_ANSWER_CONCEPT_CLASS_UUID);
        List<Concept> symptoms = conceptService.getConceptsByClass(symptomsAnswerConceptClass);

        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null,
                null, getEncounterTypesByUuids(encounterTypesUuids, encounterService), null,
                null, null, false);
        sortEncounterListByDate(encounters);

        int maxRecords = (StringUtils.isNotBlank((String) config.get(MAX_RECORDS_ATTR_NAME)))
                ? Integer.parseInt((String) config.get(MAX_RECORDS_ATTR_NAME)) : DEFAULT_MAX_RECORDS;

        model.addAttribute(PATIENT_ID, patient.getUuid());
        model.addAttribute(ENCOUNTERS_ATTR_NAME, encounters);
        model.addAttribute(MAX_RECORDS_ATTR_NAME, maxRecords);
        model.addAttribute(TEXTS_LIST_ATTR_NAME, getTrimmedSymptomsTextsList(encounters, symptoms));
        model.addAttribute(ENCOUNTERS_BOOL_LIST_ATTR_NAME, getEncounterStatusesBooleanList(encounters,
                encounterClosedStatusConcept));
    }

    private List<EncounterType> getEncounterTypesByUuids(String encounterTypesUuids, EncounterService encounterService) {
        List<String> splittedUuids = new ArrayList<String>();
        if (StringUtils.isNotBlank(encounterTypesUuids)) {
            splittedUuids = Arrays.asList(encounterTypesUuids.split(","));
        }

        List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
        for (String uuid : splittedUuids) {
            if (StringUtils.isNotBlank(uuid)) {
                encounterTypes.add(encounterService.getEncounterTypeByUuid(uuid));
            }
        }
        return encounterTypes;
    }

    private List<String> getTrimmedSymptomsTextsList(List<Encounter> encounters, List<Concept> symptoms) {
        List<String> textsList = new ArrayList<String>();
        for (Encounter encounter : encounters) {
            List<Obs> obsList = new ArrayList<Obs>(encounter.getAllObs(false));
            String commaSeparatedSymptomsList = getCommaSeparatedSymptomsList(obsList, symptoms);
            String trimmedText = trimText(commaSeparatedSymptomsList);
            textsList.add(trimmedText);
        }
        return textsList;
    }

    private String getCommaSeparatedSymptomsList(List<Obs> obsList, List<Concept> symptoms) {
        StringBuilder stringBuilder = new StringBuilder();
        String text;
        for (Obs obs : obsList) {
            if (symptoms.contains(obs.getValueCoded())) {
                text = obs.getValueCoded().getName().getName() + ", ";
                stringBuilder.append(text);
            }
        }
        return stringBuilder.toString();
    }

    private String trimText(String text) {
        String trimmedText = text.trim();
        if (trimmedText.endsWith(",")) {
            trimmedText = trimmedText.substring(0, trimmedText.length() - 1);
        }
        return trimmedText;
    }

    private List<Boolean> getEncounterStatusesBooleanList(List<Encounter> encounters, Concept encounterClosedStatusConcept) {
        List<Boolean> encountersStatusesBoolList = new ArrayList<Boolean>();
        for (Encounter encounter : encounters) {
            encountersStatusesBoolList.add(checkIfEncounterStatusClosed(encounter, encounterClosedStatusConcept));
        }
        return encountersStatusesBoolList;
    }

    private boolean checkIfEncounterStatusClosed(Encounter encounter, Concept encounterClosedStatusConcept) {
        List<Obs> obsList = new ArrayList<Obs>(encounter.getAllObs(false));
        for (Obs obs : obsList) {
            Concept obsValueCoded = obs.getValueCoded();
            if (obsValueCoded != null && obsValueCoded.equals(encounterClosedStatusConcept)) {
                return true;
            }
        }
        return false;
    }

    private void sortEncounterListByDate(List<Encounter> encounters) {
        Collections.sort(encounters, new Comparator<Encounter>() {
            @Override
            public int compare(Encounter e1, Encounter e2) {
                return e2.getDateCreated().compareTo(e1.getDateCreated());
            }
        });
    }
}
