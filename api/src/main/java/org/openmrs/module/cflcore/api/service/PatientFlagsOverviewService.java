package org.openmrs.module.cflcore.api.service;

import java.util.List;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;

public interface PatientFlagsOverviewService {

  /**
   * Gets paginated results with flagged patients with specific criteria.
   *
   * @param criteria   criteria by which patients are searched
   * @param pageNumber page number
   * @param pageSize   page size
   * @return list of {@link PatientFlagsOverviewDTO} objects
   */
  List<PatientFlagsOverviewDTO> getPatientsWithFlag(PatientFlagsOverviewCriteria criteria,
      Integer pageNumber, Integer pageSize);
}
