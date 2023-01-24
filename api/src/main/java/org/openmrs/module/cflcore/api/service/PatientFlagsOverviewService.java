/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;

public interface PatientFlagsOverviewService {

  /**
   * Gets paginated results with flagged patients with specific criteria.
   *
   * @param criteria   criteria by which patients are searched
   * @param pageNumber page number
   * @param pageSize   page size
   * @return {@link PatientFlagsOverviewDTO} object
   */
  PatientFlagsOverviewDTO getPatientsWithFlag(PatientFlagsOverviewCriteria criteria,
      Integer pageNumber, Integer pageSize);
}
