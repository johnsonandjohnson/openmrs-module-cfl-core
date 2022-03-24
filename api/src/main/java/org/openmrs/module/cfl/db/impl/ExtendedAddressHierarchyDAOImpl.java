package org.openmrs.module.cfl.db.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
import org.openmrs.module.cfl.api.model.AddressDataContent;
import org.openmrs.module.cfl.db.ExtendedAddressHierarchyDAO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** The default implementation of {@link ExtendedAddressHierarchyDAO}. */
public class ExtendedAddressHierarchyDAOImpl
    extends HibernateOpenmrsObjectDAO<AddressHierarchyEntry>
    implements ExtendedAddressHierarchyDAO {

  private static final String REMOVE_PARENTS_SQL =
      "UPDATE AddressHierarchyEntry e SET parent = null";

  private static final String DELETE_ALL_SQL = "DELETE FROM AddressHierarchyEntry e";

  private static final String REMOVE_LEVEL_PARENTS_SQL =
      "UPDATE AddressHierarchyLevel ahl SET parent = null";

  private static final String REMOVE_ALL_LEVELS_SQL = "DELETE FROM AddressHierarchyLevel ahl";

  private static final String GET_ADDRESS_DATA_SQL =
      "SELECT ahe1.name AS level1, ahe2.name AS level2, ahe3.name AS level3, ahe4.name AS level4, ahe5.name AS level5\n"
          + "FROM address_hierarchy_entry AS ahe1\n"
          + "LEFT JOIN address_hierarchy_entry AS ahe2 ON ahe2.parent_id = ahe1.address_hierarchy_entry_id\n"
          + "LEFT JOIN address_hierarchy_entry AS ahe3 ON ahe3.parent_id = ahe2.address_hierarchy_entry_id\n"
          + "LEFT JOIN address_hierarchy_entry AS ahe4 ON ahe4.parent_id = ahe3.address_hierarchy_entry_id\n"
          + "LEFT JOIN address_hierarchy_entry AS ahe5 ON ahe5.parent_id = ahe4.address_hierarchy_entry_id\n"
          + "WHERE ahe1.name IN (SELECT name from address_hierarchy_entry WHERE parent_id IS NULL)";

  private AddressHierarchyDAO addressHierarchyDAO;

  public ExtendedAddressHierarchyDAOImpl() {
    this.mappedClass = AddressHierarchyEntry.class;
  }

  @Override
  public void deleteAllAddressHierarchyEntriesSafely() {
    final Session session = this.sessionFactory.getCurrentSession();

    session.createQuery(REMOVE_PARENTS_SQL).executeUpdate();
    session.createQuery(DELETE_ALL_SQL).executeUpdate();
  }

  @Override
  public void deleteAllAddressHierarchyLevels() {
    final Session session = this.sessionFactory.getCurrentSession();

    session.createQuery(REMOVE_LEVEL_PARENTS_SQL).executeUpdate();
    session.createQuery(REMOVE_ALL_LEVELS_SQL).executeUpdate();
  }

  @Override
  public AddressDataDTO getAddressData(Integer pageNumber, Integer pageSize) {
    SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(GET_ADDRESS_DATA_SQL);
    query.setFirstResult(pageNumber - 1);
    query.setMaxResults(pageSize);
    List<Object[]> results = query.list();

    int totalCount = getCountAllAddressData();
    return new AddressDataDTO(
        pageNumber,
        pageSize,
        totalCount,
        pageNumber * pageSize < totalCount,
        mapQueryResults(results));
  }

  @Override
  public int getCountAllAddressData() {
    String sqlQuery = "SELECT COUNT(*) FROM (" + GET_ADDRESS_DATA_SQL + ") results";

    return ((BigInteger) sessionFactory.getCurrentSession().createSQLQuery(sqlQuery).uniqueResult())
        .intValue();
  }

  private List<AddressDataContent> mapQueryResults(List<Object[]> results) {
    List<AddressDataContent> addressDataContents = new ArrayList<>();
    results.forEach(objects -> addressDataContents.add(new AddressDataContent(objects)));

    return addressDataContents;
  }

  public AddressHierarchyDAO getAddressHierarchyDAO() {
    return addressHierarchyDAO;
  }

  public void setAddressHierarchyDAO(AddressHierarchyDAO addressHierarchyDAO) {
    this.addressHierarchyDAO = addressHierarchyDAO;
  }
}
