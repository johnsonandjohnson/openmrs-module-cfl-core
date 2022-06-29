/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller.dashboardwidgets;

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
    public static final String TEXTS_LIST_ATTR_NAME = "textsList";
    public static final String ENCOUNTERS_BOOL_LIST_ATTR_NAME = "encountersBoolList";
    public static final String ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID_PARAM_NAME = "encounterClosedStatusConceptUuid";
    public static final String ANSWER_CONCEPT_UUID_PARAM_NAME = "answerConceptUuid";
    public static final String OBS_ANSWER_CONCEPT_CLASS_UUIDS_PARAM_NAME = "obsAnswerConceptsClassUuids";

    public void controller(FragmentConfiguration config, FragmentModel model, @RequestParam(PATIENT_ID) Patient patient,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("conceptService") ConceptService conceptService) {

        String encounterTypesUuids = config.get(ENCOUNTER_TYPE_UUID).toString();
        Concept encounterClosedStatusConcept =
                conceptService.getConceptByUuid(config.get(ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID_PARAM_NAME).toString());
        Concept answerConcept = conceptService.getConceptByUuid(config.get(ANSWER_CONCEPT_UUID_PARAM_NAME).toString());

        String obsAnswerConceptClassUuids = config.get(OBS_ANSWER_CONCEPT_CLASS_UUIDS_PARAM_NAME).toString();
        List<ConceptClass> obsClasses = getConceptClassesByUuids(obsAnswerConceptClassUuids, conceptService);
        List<Concept> obsList = getConceptsByConceptClasses(obsClasses, conceptService);

        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null,
                null, getEncounterTypesByUuids(encounterTypesUuids, encounterService), null,
                null, null, false);
        sortEncounterListByDate(encounters);

        int maxRecords = (StringUtils.isNotBlank((String) config.get(MAX_RECORDS_ATTR_NAME)))
                ? Integer.parseInt((String) config.get(MAX_RECORDS_ATTR_NAME)) : DEFAULT_MAX_RECORDS;

        model.addAttribute(PATIENT_ID, patient.getUuid());
        model.addAttribute(ENCOUNTERS_ATTR_NAME, encounters);
        model.addAttribute(MAX_RECORDS_ATTR_NAME, maxRecords);
        model.addAttribute(TEXTS_LIST_ATTR_NAME, getTrimmedObsTextsList(encounters, obsList, answerConcept));
        model.addAttribute(ENCOUNTERS_BOOL_LIST_ATTR_NAME, getEncounterStatusesBooleanList(encounters,
                encounterClosedStatusConcept));
    }

    private List<Concept> getConceptsByConceptClasses(List<ConceptClass> conceptClasses, ConceptService conceptService) {
        List<Concept> conceptsByConceptClasses = new ArrayList<>();
        for (ConceptClass conceptClass : conceptClasses) {
            conceptsByConceptClasses.addAll(conceptService.getConceptsByClass(conceptClass));
        }

        return conceptsByConceptClasses;
    }

    private List<ConceptClass> getConceptClassesByUuids(String conceptClassUuid, ConceptService conceptService) {
        List<String> splittedUuids = new ArrayList<>();
        if (StringUtils.isNotBlank(conceptClassUuid)) {
            splittedUuids = Arrays.asList(conceptClassUuid.split(","));
        }
        List<ConceptClass> conceptClasses = new ArrayList<>();
        for (String uuid : splittedUuids) {
            conceptClasses.add(conceptService.getConceptClassByUuid(uuid));
        }

        return conceptClasses;
    }

    private List<EncounterType> getEncounterTypesByUuids(String encounterTypesUuids, EncounterService encounterService) {
        List<String> splittedUuids = new ArrayList<>();
        if (StringUtils.isNotBlank(encounterTypesUuids)) {
            splittedUuids = Arrays.asList(encounterTypesUuids.split(","));
        }

        List<EncounterType> encounterTypes = new ArrayList<>();
        for (String uuid : splittedUuids) {
            if (StringUtils.isNotBlank(uuid)) {
                encounterTypes.add(encounterService.getEncounterTypeByUuid(uuid));
            }
        }
        return encounterTypes;
    }

    private List<String> getTrimmedObsTextsList(List<Encounter> encounters, List<Concept> obsList,
                                                Concept answerConcept) {
        List<String> textsList = new ArrayList<>();
        for (Encounter encounter : encounters) {
            List<Obs> encounterObsList = new ArrayList<>(encounter.getAllObs(false));
            String commaSeparatedObsList = getCommaSeparatedObsList(encounterObsList, obsList, answerConcept);
            String trimmedText = trimText(commaSeparatedObsList);
            textsList.add(trimmedText);
        }
        return textsList;
    }

    private String getCommaSeparatedObsList(List<Obs> encounterObsList, List<Concept> obsList, Concept answerConcept) {
        StringBuilder stringBuilder = new StringBuilder();
        String text;
        for (Obs obs : encounterObsList) {
            if (obsList.contains(obs.getConcept()) && obs.getValueCoded().equals(answerConcept)) {
                text = obs.getConcept().getName().getName() + ", ";
                stringBuilder.append(text);
            } else if (obsList.contains(obs.getValueCoded())) {
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
        List<Boolean> encountersStatusesBoolList = new ArrayList<>();
        for (Encounter encounter : encounters) {
            encountersStatusesBoolList.add(checkIfEncounterStatusClosed(encounter, encounterClosedStatusConcept));
        }
        return encountersStatusesBoolList;
    }

    private boolean checkIfEncounterStatusClosed(Encounter encounter, Concept encounterClosedStatusConcept) {
        List<Obs> obsList = new ArrayList<>(encounter.getAllObs(false));
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
