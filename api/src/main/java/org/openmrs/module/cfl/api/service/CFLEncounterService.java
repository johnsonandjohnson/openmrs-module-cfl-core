package org.openmrs.module.cfl.api.service;

import org.openmrs.Encounter;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CFLEncounterService {

    List<Encounter> getEncountersByPatientAndEncounterTypeAndForm(Integer patientId, String encounterTypeUuid, 
                                                                  String formUuid);

    Encounter createEncounter(Integer patientId, String encounterTypeUuid, String formUuid, Map<String, String> obsMap,
                              String comment) throws ParseException;

    Encounter saveEncounter(Encounter encounter);

    Encounter createObsWithGivenEncounter(Integer patientId, Integer encounterId, String stringConceptName, String answer,
                                          String comment) throws ParseException;
}
