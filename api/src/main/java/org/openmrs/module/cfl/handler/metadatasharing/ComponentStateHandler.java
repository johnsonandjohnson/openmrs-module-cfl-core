/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.handler.metadatasharing;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appframework.AppFrameworkActivator;
import org.openmrs.module.appframework.domain.ComponentState;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ComponentStateHandler exposes {@link ComponentState} entity to Metadata Sharing module for export and import.
 * <p>
 * This bean is configured in moduleApplicationContext.xml
 * </p>
 * <p>
 * The {@link ComponentState} has confusing ID property, the `componentStateId` is used wherever possible.
 * This class saves entities by direct usage of Hibernate Session (from DbSessionFactory), it will perform flush and
 * refresh app context for each save. It's not efficient, but it was not possible to add code which would run after all
 * items. The items imported by this handler depends on items imported by {@link UserAppHandler}
 * (see {@link MetadataPriorityDependenciesHandler}).
 * </p>
 */
public class ComponentStateHandler implements MetadataTypesHandler<ComponentState>, MetadataSearchHandler<ComponentState>,
        MetadataSaveHandler<ComponentState>, MetadataPropertiesHandler<ComponentState>,
        MetadataPriorityDependenciesHandler<ComponentState> {
  private static final int HIGHER_THEN_BUILD_IN = 10;
  private final Map<Class<? extends ComponentState>, String> types;

  private DbSessionFactory sessionFactory;
  private AppFrameworkActivator appFrameworkActivator;

  public ComponentStateHandler() {
    this(new AppFrameworkActivator());
  }

  public ComponentStateHandler(final AppFrameworkActivator appFrameworkActivator) {
    final Map<Class<? extends ComponentState>, String> tmpTypes = new HashMap<Class<? extends ComponentState>, String>();
    tmpTypes.put(ComponentState.class, "Apps State (All)");
    this.types = Collections.unmodifiableMap(tmpTypes);
    this.appFrameworkActivator = appFrameworkActivator;
  }

  public void setSessionFactory(DbSessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Map<Class<? extends ComponentState>, String> getTypes() {
    return types;
  }

  @Override
  public Integer getId(ComponentState object) {
    return object.getComponentStateId();
  }

  @Override
  public void setId(ComponentState object, Integer id) {
    object.setComponentStateId(id);
  }

  @Override
  public String getUuid(ComponentState object) {
    return object.getUuid();
  }

  @Override
  public void setUuid(ComponentState object, String uuid) {
    object.setUuid(uuid);
  }

  @Override
  public Boolean getRetired(ComponentState object) {
    return Boolean.FALSE;
  }

  @Override
  public void setRetired(ComponentState object, Boolean retired) {
    // Nothing to do
  }

  @Override
  public String getName(ComponentState object) {
    return object.getComponentId();
  }

  @Override
  public String getDescription(ComponentState object) {
    return null;
  }

  @Override
  public Date getDateChanged(ComponentState object) {
    return null;
  }

  @Override
  public Map<String, Object> getProperties(ComponentState object) {
    final Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("Component type", object.getComponentType());
    properties.put("Enabled", object.getEnabled());
    return properties;
  }

  @Override
  public ComponentState saveItem(ComponentState item) {
    final ComponentState saved;

    final ComponentState existing = getItemById(ComponentState.class, item.getComponentStateId());

    if (existing == null) {
      saved = item;
    } else {
      existing.setComponentId(item.getComponentId());
      existing.setUuid(item.getUuid());
      existing.setComponentType(item.getComponentType());
      existing.setEnabled(item.getEnabled());
      saved = existing;
    }

    sessionFactory.getCurrentSession().saveOrUpdate(saved);
    // Inefficient but required
    sessionFactory.getCurrentSession().flush();
    appFrameworkActivator.contextRefreshed();
    return saved;
  }

  @Override
  public int getItemsCount(Class<? extends ComponentState> type, boolean includeRetired, String phrase) {
    return ((Number) getCriteriaForAll().setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  private Criteria getCriteriaForAll() {
    return sessionFactory.getCurrentSession().createCriteria(ComponentState.class);
  }

  @Override
  public List<ComponentState> getItems(Class<? extends ComponentState> type, boolean includeRetired, String phrase,
                                       Integer firstResult, Integer maxResults) {
    final Criteria allCriteria = getCriteriaForAll();

    if (firstResult != null) {
      allCriteria.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      allCriteria.setMaxResults(maxResults);
    }

    return allCriteria.list();
  }

  @Override
  public ComponentState getItemByUuid(Class<? extends ComponentState> type, String uuid) {
    return (ComponentState) getCriteriaForAll().add(Restrictions.eq("uuid", uuid)).uniqueResult();
  }

  @Override
  public ComponentState getItemById(Class<? extends ComponentState> type, Integer id) {
    return (ComponentState) getCriteriaForAll().add(Restrictions.eq("componentStateId", id)).uniqueResult();
  }

  @Override
  public int getPriority() {
    return HIGHER_THEN_BUILD_IN;
  }

  @Override
  public List<Object> getPriorityDependencies(ComponentState object) {
    return sessionFactory.getCurrentSession().createCriteria(UserApp.class)
            .add(Restrictions.eq("appId", object.getComponentId())).list();
  }
}
