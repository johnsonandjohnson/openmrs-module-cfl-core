package org.openmrs.module.cfl.db.impl;

import org.hibernate.Session;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.cfl.db.ExtendedConceptDataDAO;

public class ExtendedConceptDataDAOImpl extends HibernateOpenmrsObjectDAO<ConceptSet>
    implements ExtendedConceptDataDAO {

  private static final String DELETE_CONCEPT_SETS =
      "DELETE FROM ConceptSet cs WHERE cs.concept = :concept";

  @Override
  public void deleteConceptSetsByConcept(Concept concept) {
    final Session session = this.sessionFactory.getCurrentSession();

    session.createQuery(DELETE_CONCEPT_SETS).setParameter("concept", concept).executeUpdate();
  }
}
