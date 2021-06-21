package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Visit;

public interface IrisVisitService {

    Visit saveVisit(Visit visit);

    void createFutureVisits(Visit visit);

    void voidFutureVisits(Patient patient);

    void updateVisitsForRegimenChange(Visit latestDosingVisit, Patient patient);

}
