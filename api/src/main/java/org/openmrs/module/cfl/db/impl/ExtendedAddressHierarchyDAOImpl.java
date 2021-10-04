package org.openmrs.module.cfl.db.impl;

import org.hibernate.Session;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cfl.db.ExtendedAddressHierarchyDAO;

/**
 * The default implementation of {@link ExtendedAddressHierarchyDAO}.
 */
public class ExtendedAddressHierarchyDAOImpl extends HibernateOpenmrsObjectDAO<AddressHierarchyEntry>
        implements ExtendedAddressHierarchyDAO {

    private static final String REMOVE_PARENTS_SQL = "UPDATE AddressHierarchyEntry e SET parent = null";
    private static final String DELETE_ALL_SQL = "DELETE FROM AddressHierarchyEntry e";

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

    public AddressHierarchyDAO getAddressHierarchyDAO() {
        return addressHierarchyDAO;
    }

    public void setAddressHierarchyDAO(AddressHierarchyDAO addressHierarchyDAO) {
        this.addressHierarchyDAO = addressHierarchyDAO;
    }
}
