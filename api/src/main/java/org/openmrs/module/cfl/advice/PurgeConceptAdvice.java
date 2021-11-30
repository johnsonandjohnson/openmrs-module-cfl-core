package org.openmrs.module.cfl.advice;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.db.ExtendedConceptDataDAO;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class PurgeConceptAdvice implements MethodBeforeAdvice {

  private static final String PURGE_CONCEPT_METHOD_NAME = "purgeConcept";

  @Override
  public void before(Method method, Object[] objects, Object o) throws Throwable {
    if (method.getName().equals(PURGE_CONCEPT_METHOD_NAME)) {
      removeRelatedConceptSets(objects[0]);
    }
  }

  private void removeRelatedConceptSets(Object o) {
    if (o instanceof Concept) {
      Concept concept = (Concept) o;
      Context.getRegisteredComponent(
              CFLConstants.EXTENDED_CONCEPT_DATA_DAO_BEAN_NAME, ExtendedConceptDataDAO.class)
          .deleteConceptSetsByConcept(concept);
    }
  }
}
