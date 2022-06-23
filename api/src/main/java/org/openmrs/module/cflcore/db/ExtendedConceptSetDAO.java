/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db;

import org.openmrs.Concept;

/**
 * The ExtendedConceptDataDAO interface.
 *
 * <p>Provides additional custom methods related to concept resources.
 */
public interface ExtendedConceptSetDAO {

  /**
   * Removes concept sets related to the concept. It is performed before calling purgeConcept method
   * because when the concept is permanently removed from database, in concepts sets there is still
   * reference to it and during concept sets fetching it throws exceptions.
   *
   * @param concept concept that is supposed to be removed
   */
  void deleteConceptSetsByConcept(Concept concept);
}
