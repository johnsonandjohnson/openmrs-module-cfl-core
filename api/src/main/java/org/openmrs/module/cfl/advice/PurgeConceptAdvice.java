package org.openmrs.module.cfl.advice;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.db.ExtendedConceptSetDAO;
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
