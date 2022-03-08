package org.openmrs.module.cfl.api.service;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * CFLEncounterService which provides custom methods related to encounter stuff
 */
public interface CFLEncounterService {

    /**
     * Gets encounters by patientId, encounterTypeUuid and formUuid
     *
     * @param patientId patient id related to encounter
     * @param encounterTypeUuid encounter type uuid related to encounter
     * @param formUuid form uuid related to encounter
     * @return list of encounters
     */
    List<Encounter> getEncountersByPatientAndEncounterTypeAndForm(Integer patientId, String encounterTypeUuid, 
                                                                  String formUuid);

    /**
     * Creates and saves encounter by patientId, encounterTypeUuid, formUuid, obsMap, comment
     *
     * @param patientId patient id related to encounter
     * @param encounterTypeUuid encounter type uuid related to encounter
     * @param formUuid form uuid related to encounter
     * @param obsMap obs map related to encounter
     * @param comment obs comment
     * @return encounter
     * @throws ParseException if datetime value for obs cannot be parsed properly
     */
    Encounter createEncounter(Integer patientId, String encounterTypeUuid, String formUuid, Map<String, String> obsMap,
                              String comment) throws ParseException;

    /**
     * Saves encounter in database
     *
     * @param encounter encounter to save
     * @return saved encounter
     */
    Encounter saveEncounter(Encounter encounter);


    /**
     * Creates obs and assigns it to encounter (if exists)
     *
     * @param patientId patient id related to encounter
     * @param encounterId encounter id related to encounter
     * @param stringConceptName name of concept related to obs
     * @param answer concept answer
     * @param comment obs comment
     * @return encounter or null (if encounter with encounterId does not exist)
     * @throws ParseException if datetime value for obs cannot be parsed properly
     */
    Encounter createObsWithGivenEncounter(Integer patientId, Integer encounterId, String stringConceptName, String answer,
                                          String comment) throws ParseException;

    /**
     * Gets encounters by patient and encounter type
     *
     * @param patient patient related to encounter
     * @param encounterType encounter type related to encounter
     * @return list of encounters
     */
    List<Encounter> getEncountersByPatientAndType(Patient patient, EncounterType encounterType);
}
