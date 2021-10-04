package org.openmrs.module.cfl.db;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cfl.db.impl.ExtendedAddressHierarchyDAOImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ExtendedAddressHierarchyDAOImplTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Qualifier("testHibernateAddressHierarchyDAO")
    @Autowired
    private AddressHierarchyDAO addressHierarchyDAO;

    public ExtendedAddressHierarchyDAOImplTest() throws Exception {
        // Skip because it imports some Patients
        skipBaseSetup();
    }

    @Before
    public void beforeTest() throws Exception {
        executeDataSet("org/openmrs/include/initialInMemoryTestDataSet.xml");
        executeDataSet("datasets/ExtendedAddressHierarchyDAOImplTest.xml");

        this.authenticate();
    }

    @Test
    public void deleteAllAddressHierarchyEntriesSafely_shouldDeleteRootAndChildren() {
        // given
        final ExtendedAddressHierarchyDAOImpl impl = new ExtendedAddressHierarchyDAOImpl();
        impl.setSessionFactory(sessionFactory);
        impl.setAddressHierarchyDAO(addressHierarchyDAO);

        // when
        impl.deleteAllAddressHierarchyEntriesSafely();

        // then
        Assert.assertEquals(0, doCountQuery(AddressHierarchyEntry.class));
    }

    private int doCountQuery(Class<?> entityClass) {
        final Number rawResult = (Number) sessionFactory
                .getCurrentSession()
                .createCriteria(entityClass)
                .setProjection(Projections.rowCount())
                .uniqueResult();

        Assert.assertNotNull("Count for class: " + entityClass.getSimpleName() + " should not return empty result!",
                rawResult);

        return rawResult.intValue();
    }
}
