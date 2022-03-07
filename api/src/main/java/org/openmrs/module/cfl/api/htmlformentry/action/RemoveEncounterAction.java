package org.openmrs.module.cfl.api.htmlformentry.action;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.service.CFLEncounterService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RemoveEncounterAction implements CustomFormSubmissionAction {

  private static final String ENCOUNTER_VOIDING_REASON = "Replaced by a new encounter";

  @Override
  public void applyAction(FormEntrySession formEntrySession) {
    Encounter currentEncounter = formEntrySession.getEncounter();
    List<Encounter> encounters =
        Context.getService(CFLEncounterService.class)
            .getEncountersByPatientAndType(
                currentEncounter.getPatient(), currentEncounter.getEncounterType());
    Optional<Encounter> encounterToVoid = findEncounterToVoid(encounters);
    encounterToVoid.ifPresent(
        value -> Context.getEncounterService().voidEncounter(value, ENCOUNTER_VOIDING_REASON));
  }

  private Optional<Encounter> findEncounterToVoid(List<Encounter> encounters) {
    if (encounters.size() > 1) {
      return encounters.stream().min(Comparator.comparing(Encounter::getEncounterId));
    }
    return Optional.empty();
  }
}
