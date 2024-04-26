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

import org.openmrs.Person;

/**
 * Custom Administration service.
 *
 * <p>Used as a base for other services or AOP classes that e.g. can capture data passed from below
 * methods (e.g. Patient param)
 */
public interface CustomAdministrationService {

  /**
   * Gets global property (using regular OpenMRS Administration Service underneath). Person param
   * is helpful for other services/AOP classes that can capture patient object and perform some
   * specific actions (e.g. searching another global property based on person location/project/any
   * other things)
   *
   * @param key global property key
   * @param person person object
   * @return value of global property
   */
  String getGlobalProperty(String key, Person person);
}
