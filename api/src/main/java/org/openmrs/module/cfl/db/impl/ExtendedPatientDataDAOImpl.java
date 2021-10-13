package org.openmrs.module.cfl.db.impl;

import org.hibernate.Session;
import org.openmrs.PersonAttribute;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.db.ExtendedPatientDataDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The default implementation of {@link ExtendedPatientDataDAO}.
 */
public class ExtendedPatientDataDAOImpl extends HibernateOpenmrsObjectDAO<PersonAttribute>
        implements ExtendedPatientDataDAO {

    private static final String RETURN_VACCINE_NAMES_LINKED_WITH_ANY_PATIENT =
            "SELECT DISTINCT(pa.value) " +
            "FROM PersonAttribute pa " +
            "INNER JOIN pa.attributeType pat " +
            "INNER JOIN pa.person p " +
            "WHERE p.dead = false " +
            "AND p.voided = false " +
            "AND pa.voided = false " +
            "AND pat.name = :attributeTypeName";

    @Transactional(readOnly = true)
    @Override
    public List<String> getVaccineNamesLinkedToAnyPatient() {
        final Session session = this.sessionFactory.getCurrentSession();

        return session
                .createQuery(RETURN_VACCINE_NAMES_LINKED_WITH_ANY_PATIENT)
                .setParameter("attributeTypeName", CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME)
                .list();
    }
}
