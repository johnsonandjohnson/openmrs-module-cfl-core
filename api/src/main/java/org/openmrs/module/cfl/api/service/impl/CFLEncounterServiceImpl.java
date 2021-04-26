package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.cfl.api.service.CFLEncounterService;
import org.openmrs.module.cfl.api.util.DateUtil;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CFLEncounterServiceImpl implements CFLEncounterService {

    private EncounterService encounterService;

    private PatientService patientService;

    private ConceptService conceptService;

    private FormService formService;

    private ObsService obsService;

    @Override
    public List<Encounter> getEncountersByPatientAndEncounterTypeAndForm(Integer patientId, String encounterTypeUuid,
                                                                         String formUuid) {
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUuid);
        Form form = formService.getFormByUuid(formUuid);

        List<Encounter> patientEncounters = encounterService.getEncounters(patientService.getPatient(patientId),
                null, null, null, Arrays.asList(form), Arrays.asList(encounterType),
                null, null, null, false);
        sortEncounterListByDate(patientEncounters);

        return patientEncounters;
    }

    @Override
    public Encounter createEncounter(Integer patientId, String encounterTypeUuid, String formUuid,
                                     Map<String, String> obsMap, String comment) throws ParseException {
        Patient patient = patientService.getPatient(patientId);

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(DateUtil.now());
        encounter.setEncounterType(encounterService.getEncounterTypeByUuid(encounterTypeUuid));
        encounter.setForm(formService.getFormByUuid(formUuid));
        encounter.setLocation(patient.getPatientIdentifier().getLocation());
        encounter.addProvider(new EncounterRole(1), new Provider(1));

        for (Map.Entry<String, String> entry : obsMap.entrySet()) {
            encounter.addObs(createObs(patientId, entry.getKey(), entry.getValue(), comment));
        }

        return saveEncounter(encounter);
    }

    @Override
    public Encounter saveEncounter(Encounter encounter) {
        return encounterService.saveEncounter(encounter);
    }

    @Override
    public Encounter createObsWithGivenEncounter(Integer patientId, Integer encounterId, String stringConceptName,
                                                 String answer, String comment) throws ParseException {
        Obs obs = createObs(patientId, stringConceptName, answer, comment);

        Encounter encounter = null;
        if (encounterId != null) {
            encounter = encounterService.getEncounter(encounterId);
            obs.setEncounter(encounter);
            obsService.saveObs(obs, comment);
        }

        return encounter;
    }

    private Obs createObs(Integer patientId, String stringConceptName, String answer, String comment) throws ParseException {
        Obs obs = new Obs();
        obs.setPerson(patientService.getPatient(patientId));
        obs.setConcept(conceptService.getConceptByName(stringConceptName));
        if (conceptService.getConceptByName(answer) != null) {
            obs.setValueCoded(conceptService.getConceptByName(answer));
        } else {
            obs.setValueAsString(answer);
        }
        obs.setObsDatetime(DateUtil.now());
        obs.setComment(comment);

        return obs;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public void setFormService(FormService formService) {
        this.formService = formService;
    }

    public void setObsService(ObsService obsService) {
        this.obsService = obsService;
    }

    private void sortEncounterListByDate(List<Encounter> encounters) {
        Collections.sort(encounters, new Comparator<Encounter>() {
            @Override
            public int compare(Encounter e1, Encounter e2) {
                return e2.getEncounterDatetime().compareTo(e1.getEncounterDatetime());
            }
        });
    }
}
