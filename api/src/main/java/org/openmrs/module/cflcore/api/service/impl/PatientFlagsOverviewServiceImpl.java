/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.api.service.PatientFlagsOverviewService;
import org.openmrs.module.cflcore.db.PatientFlagsOverviewDAO;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.springframework.transaction.annotation.Transactional;

public class PatientFlagsOverviewServiceImpl implements PatientFlagsOverviewService {

  private static final Log LOGGER = LogFactory.getLog(PatientFlagsOverviewServiceImpl.class);
  
  private PatientFlagsOverviewDAO patientFlagsOverviewDAO;

  @Override
  @Transactional(readOnly = true)
  public PatientFlagsOverviewDTO getPatientsWithFlag(PatientFlagsOverviewCriteria criteria,
      Integer pageNumber, Integer pageSize) {
    try {
      return patientFlagsOverviewDAO.getPatientsWithFlag(criteria, pageNumber, pageSize);
    } catch (Exception exception) {
      LOGGER.error("Failed to read flagged patients for flag: " + criteria.getFlagName(),exception);
      return null;
    }
  }

  public void setPatientFlagsOverviewDAO(
      PatientFlagsOverviewDAO patientFlagsOverviewDAO) {
    this.patientFlagsOverviewDAO = patientFlagsOverviewDAO;
  }
}
