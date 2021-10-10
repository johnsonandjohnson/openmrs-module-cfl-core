package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class CFLPatientServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute> implements CFLPatientService {

    private static final String VALUE_PROPERTY_NAME = "value";

    private static final String VOIDED_PROPERTY_NAME = "voided";

    private static final String PERSON_PROPERTY_NAME = "person";

    private static final String DEAD_PROPERTY_NAME = "dead";

    private static final String PERSON_ATTRIBUTE_NAME = "personAttribute";

    private DbSessionFactory sessionFactory;

    public CFLPatientServiceImpl() {
        super(PersonAttribute.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Patient> findByVaccinationName(String vaccinationName) {

        List<PersonAttribute> personAttributes = getPersonAttributeList(vaccinationName);

        List<Patient> patientList = new ArrayList<>();
        for (PersonAttribute personAttribute : personAttributes) {
            patientList.add(new Patient(personAttribute.getPerson()));
        }

        return patientList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getVaccineNamesLinkedToAnyPatient() {
        String query = "SELECT DISTINCT(value) from person_attribute pa " +
                "INNER JOIN person_attribute_type pat ON pa.person_attribute_type_id = pat.person_attribute_type_id " +
                "INNER JOIN person p ON pa.person_id = p.person_id " +
                "WHERE p.dead = 0 " +
                "AND p.voided = 0 " +
                "AND pa.voided = 0 " +
                "AND pat.name = '" + CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME + "'";

        return getSession().createSQLQuery(query).list();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    private List<PersonAttribute> getPersonAttributeList(String vaccinationName) {
        Criteria criteria = getSession().createCriteria(this.mappedClass, PERSON_ATTRIBUTE_NAME);
        criteria.createAlias(PERSON_ATTRIBUTE_NAME + "." + PERSON_PROPERTY_NAME, PERSON_PROPERTY_NAME);
        criteria.add(Restrictions.eq(VALUE_PROPERTY_NAME, vaccinationName));
        criteria.add(Restrictions.eq(VOIDED_PROPERTY_NAME, false));
        criteria.add(Restrictions.eq(PERSON_PROPERTY_NAME + "." + VOIDED_PROPERTY_NAME, false));
        criteria.add(Restrictions.eq(PERSON_PROPERTY_NAME + "." + DEAD_PROPERTY_NAME, false));
        return criteria.list();
    }
}
