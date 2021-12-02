package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.openmrs.module.cfl.db.ExtendedPatientDataDAO;
import org.openmrs.module.locationbasedaccess.LocationBasedAccessConstants;
import org.openmrs.module.locationbasedaccess.utils.LocationUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CFLPatientServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute>
    implements CFLPatientService {

  private static final String VALUE_PROPERTY_NAME = "value";

  private static final String VOIDED_PROPERTY_NAME = "voided";

  private static final String PERSON_PROPERTY_NAME = "person";

  private static final String DEAD_PROPERTY_NAME = "dead";

  private static final String PERSON_ATTRIBUTE_NAME = "personAttribute";

  private DbSessionFactory sessionFactory;
  private PatientDAO patientDAO;
  private ExtendedPatientDataDAO extendedPatientDataDAO;

  public CFLPatientServiceImpl() {
    super(PersonAttribute.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Patient> findByVaccinationName(String vaccinationName) {
    final List<PersonAttribute> personAttributes = getPersonAttributeList(vaccinationName);

    final List<Patient> patientList = new ArrayList<>();
    for (PersonAttribute personAttribute : personAttributes) {
      patientList.add(new Patient(personAttribute.getPerson()));
    }

    return patientList;
  }

  @Override
  public Long getCountOfPatients(String query) {
    final Optional<Set<String>> currentUserLocationUuids = getCurrentUserLocationUuids();

    if (currentUserLocationUuids.isPresent()) {
      return getExtendedPatientDataDAO()
          .getCountOfPatientsInLocations(query, currentUserLocationUuids.get());
    } else {
      return patientDAO.getCountOfPatients(query);
    }
  }

  @Override
  public Long getCountOfPatients(String query, boolean includeVoided) {
    final Optional<Set<String>> currentUserLocationUuids = getCurrentUserLocationUuids();

    if (currentUserLocationUuids.isPresent()) {
      return getExtendedPatientDataDAO()
          .getCountOfPatientsInLocations(query, includeVoided, currentUserLocationUuids.get());
    } else {
      return patientDAO.getCountOfPatients(query, includeVoided);
    }
  }

  public void setSessionFactory(DbSessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private DbSession getSession() {
    return sessionFactory.getCurrentSession();
  }

  public PatientDAO getPatientDAO() {
    return patientDAO;
  }

  public void setPatientDAO(PatientDAO patientDAO) {
    this.patientDAO = patientDAO;
  }

  public ExtendedPatientDataDAO getExtendedPatientDataDAO() {
    return extendedPatientDataDAO;
  }

  public void setExtendedPatientDataDAO(ExtendedPatientDataDAO extendedPatientDataDAO) {
    this.extendedPatientDataDAO = extendedPatientDataDAO;
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

  private Optional<Set<String>> getCurrentUserLocationUuids() {
    final Optional<User> authenticatedUser = getAuthenticatedUser();
    final boolean restrictLocations =
        authenticatedUser.map(this::isUserWithRestrictedLocations).orElse(false);

    return restrictLocations
        ? getAuthenticatedUser().flatMap(this::getUserAccessibleLocationUuids)
        : Optional.empty();
  }

  private boolean isUserWithRestrictedLocations(User authenticatedUser) {
    final String locationBasedAccessRestriction =
        Context.getAdministrationService()
            .getGlobalProperty(
                LocationBasedAccessConstants.PATIENT_RESTRICTION_GLOBAL_PROPERTY_NAME);

    return !Daemon.isDaemonUser(authenticatedUser)
        && !authenticatedUser.isSuperUser()
        && Boolean.parseBoolean(locationBasedAccessRestriction);
  }

  private Optional<User> getAuthenticatedUser() {
    return Optional.ofNullable(Context.getAuthenticatedUser());
  }

  private Optional<Set<String>> getUserAccessibleLocationUuids(User authenticatedUser) {
    return Optional.ofNullable(LocationUtils.getUserAccessibleLocationUuids(authenticatedUser))
        .map(HashSet::new);
  }
}
