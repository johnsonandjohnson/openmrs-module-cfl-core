package org.openmrs.module.cfl.db;

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
