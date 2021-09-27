package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.cfl.api.dto.RegimensPatientsDataDTO;

import java.util.Date;
import java.util.List;

public interface VaccinationService {
    /**
     * Creates future visits which should follow the occurred {@code updatedVisit} according to the vaccination program
     * defined in
     * Global Property {@link org.openmrs.module.cfl.CFLConstants#VACCINATION_PROGRAM_KEY}.
     * <p>
     * The {@code occurrenceDateTime} date is used as a base to calculate dates of the future visits.
     * </p>
     * <p>
     * If the {@code updatedVisit} is the last visit in vaccination program, then this method does nothing.
     * </p>
     *
     * @param updatedVisit       the visit which has occurred, not null
     * @param occurrenceDateTime the date time when the {@code visit} has occurred, not null
     */
    void createFutureVisits(Visit updatedVisit, Date occurrenceDateTime);

    /**
     * Voids all the pending scheduled visits of the patient
     *
     * @param patient - the patient whose visits are to be voided
     */
    void voidFutureVisits(Patient patient);

    /**
     * Voids the scheduled visits and creates future visits
     *
     * @param latestDosingVisit the latest occurred dosing visit
     * @param patient the patient whose visits are to be rescheduled for regimen change
     */
    void rescheduleVisits(Visit latestDosingVisit, Patient patient);

    /**
     * Finds vaccination programs which have been changed and related patients.
     * Then voids the scheduled visits and recreates future visits based on those regimens changes.
     *
     * @param previousVaccineGPValue previous version of vaccines value
     * @param currentVaccineGPValue current version of vaccines value
     */
    void rescheduleVisitsBasedOnRegimenChanges(String previousVaccineGPValue, String currentVaccineGPValue);

    /**
     * Retrieves information about available regimens and related patients and adapts results
     * to {@link RegimensPatientsDataDTO} structure
     *
     * @param regimenGP - name of regimen global property
     *
     * @return - results adapted to {@link RegimensPatientsDataDTO} structure
     */
    List<RegimensPatientsDataDTO> getResultsList(String regimenGP);

    /**
     * Reschedules (voids and recreates) future visits based on patient's regimen configuration
     *
     * @param patient the patient whose visits are to be rescheduled
     */
    void rescheduleRegimenVisitsByPatient(Patient patient);
}
