/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cflcore.db.impl.ExtendedAddressHierarchyDAOImpl;
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
