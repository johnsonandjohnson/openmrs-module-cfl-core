package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Visit;

public interface IrisVisitService {
    Visit saveVisit(Visit visit);

    void createFutureVisits(Visit updatedVisit);

    void voidFutureVisits(Patient patient);

}
