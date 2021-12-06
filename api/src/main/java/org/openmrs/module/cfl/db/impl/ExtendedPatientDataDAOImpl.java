package org.openmrs.module.cfl.db.impl;

import org.hibernate.Session;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.db.ExtendedPatientDataDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** The default implementation of {@link ExtendedPatientDataDAO}. */
public class ExtendedPatientDataDAOImpl extends HibernateOpenmrsObjectDAO<PersonAttribute>
    implements ExtendedPatientDataDAO {

  private static final String RETURN_VACCINE_NAMES_LINKED_WITH_ANY_PATIENT =
      "SELECT DISTINCT(pa.value) "
          + "FROM PersonAttribute pa "
          + "INNER JOIN pa.attributeType pat "
          + "INNER JOIN pa.person p "
          + "WHERE p.dead = false "
          + "AND p.voided = false "
          + "AND pa.voided = false "
          + "AND pat.name = :attributeTypeName";

  private PatientDAO patientDAO;

  @Transactional(readOnly = true)
  @Override
  public List<String> getVaccineNamesLinkedToAnyPatient() {
    final Session session = this.sessionFactory.getCurrentSession();

    return session
        .createQuery(RETURN_VACCINE_NAMES_LINKED_WITH_ANY_PATIENT)
        .setParameter("attributeTypeName", CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME)
        .list();
  }

  @Override
  public Long getCountOfPatientsInLocations(String query, Set<String> locationUuids) {
    return getCountOfPatientsInLocations(query, false, locationUuids);
  }

  @Override
  public Long getCountOfPatientsInLocations(
      String query, boolean includeVoided, Set<String> locationUuids) {
    // The implementation here is done in this naive way, because the OpenMRS design doesn't allow
    // us to use their low level querying utilities. Additionally, they've made some refactoring
    // between 2.0 and 2.3. At the end, it's either this or a complete reimplementation of the
    // OpenMRS query.
    final List<Patient> patientsForQuery = getPatientsForQuery(query, includeVoided);

    final Optional<PersonAttributeType> locationPersonAttribute = getLocationPersonAttribute();
    final long countOfPatients;

    if (locationPersonAttribute.isPresent()) {
      countOfPatients =
          countPatientsInLocation(patientsForQuery, locationPersonAttribute.get(), locationUuids);
    } else {
      countOfPatients = patientsForQuery.size();
    }

    return countOfPatients;
  }

  public PatientDAO getPatientDAO() {
    return patientDAO;
  }

  public void setPatientDAO(PatientDAO patientDAO) {
    this.patientDAO = patientDAO;
  }

  private List<Patient> getPatientsForQuery(String query, boolean includeVoided) {
    return patientDAO.getPatients(query, includeVoided, 0, null);
  }

  private Optional<PersonAttributeType> getLocationPersonAttribute() {
    final String locationAttributeUuid =
        Context.getAdministrationService()
            .getGlobalProperty(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME);

    return Optional.ofNullable(locationAttributeUuid)
        .map(Context.getPersonService()::getPersonAttributeTypeByUuid);
  }

  private long countPatientsInLocation(
      List<Patient> patientsForQuery,
      PersonAttributeType locationPersonAttribute,
      Set<String> locationUuids) {
    return patientsForQuery.stream()
        .filter(patient -> isPatientInAnyLocation(patient, locationPersonAttribute, locationUuids))
        .count();
  }

  private static Boolean isPatientInAnyLocation(
      Patient patient,
      PersonAttributeType patientLocationAttributeType,
      Set<String> locationsUuidToFilter) {
    final PersonAttribute locationAttribute = patient.getAttribute(patientLocationAttributeType);
    return (locationAttribute != null
        && locationsUuidToFilter.contains(locationAttribute.getValue()));
  }
}
