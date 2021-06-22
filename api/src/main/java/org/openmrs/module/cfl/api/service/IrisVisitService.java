package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Visit;

public interface IrisVisitService {
    Visit saveVisit(Visit visit);

    /**
     * creates future visits for the person's vaccination regimen
     * @param updatedVisit - the last occurred visit of the patient
     */
    void createFutureVisits(Visit updatedVisit);

    /**
     * voids all the pending scheduled visits of the patient
     * @param patient - the patient whose visits are to be voided
     */
    void voidFutureVisits(Patient patient);

}
