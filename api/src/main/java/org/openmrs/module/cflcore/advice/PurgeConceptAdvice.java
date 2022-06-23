/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.advice;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.db.ExtendedConceptSetDAO;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class PurgeConceptAdvice implements MethodBeforeAdvice {

  private static final String PURGE_CONCEPT_METHOD_NAME = "purgeConcept";

  /**
   * This method is performed before calling purgeConcept method from ConceptService because when
   * the concept is permanently removed from database, in concepts sets there is still a reference
   * to it and during concept sets fetching it throws exceptions.
   */
  @Override
  public void before(Method method, Object[] objects, Object o) throws Throwable {
    if (method.getName().equals(PURGE_CONCEPT_METHOD_NAME)) {
      removeRelatedConceptSets((Concept) objects[0]);
    }
  }

  private void removeRelatedConceptSets(Concept concept) {
    Context.getRegisteredComponent(
            CFLConstants.EXTENDED_CONCEPT_SET_DAO_BEAN_NAME, ExtendedConceptSetDAO.class)
        .deleteConceptSetsByConcept(concept);
  }
}
