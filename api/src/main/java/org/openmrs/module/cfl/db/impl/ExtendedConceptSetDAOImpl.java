/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.db.impl;

import org.hibernate.Session;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.cfl.db.ExtendedConceptSetDAO;

public class ExtendedConceptSetDAOImpl extends HibernateOpenmrsObjectDAO<ConceptSet>
    implements ExtendedConceptSetDAO {

  private static final String DELETE_CONCEPT_SETS =
      "DELETE FROM ConceptSet cs WHERE cs.concept = :concept";

  @Override
  public void deleteConceptSetsByConcept(Concept concept) {
    final Session session = this.sessionFactory.getCurrentSession();

    session.createQuery(DELETE_CONCEPT_SETS).setParameter("concept", concept).executeUpdate();
  }
}
