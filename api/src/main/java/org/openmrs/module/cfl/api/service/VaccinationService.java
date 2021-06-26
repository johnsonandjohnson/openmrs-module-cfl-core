package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.Date;

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
     * voids all the pending scheduled visits of the patient
     *
     * @param patient - the patient whose visits are to be voided
     */
    void voidFutureVisits(Patient patient);

    /**
     * void the scheduled visits and create future visits
     *
     * @param latestDosingVisit the latest occurred dosing visit
     * @param patient the patient whose visits are to be rescheduled for regimen change
     */
    void rescheduleVisits(Visit latestDosingVisit, Patient patient);
}
