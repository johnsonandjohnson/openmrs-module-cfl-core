/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.handler.metadatasharing;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appframework.AppFrameworkActivator;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The UserAppHandler exposes {@link UserApp} entity to Metadata Sharing module for export and import.
 * <p>
 * This bean is configured in moduleApplicationContext.xml
 * </p>
 * <p>
 * The {@link UserApp} has no ID nor UUID, for both the String `appId` is used: its hashcode for Integer ID and reliably
 * generates UUID from the 'appId'.
 * This class saves entities by direct usage of Hibernate Session (from DbSessionFactory), it will perform flush and
 * refresh app context for each save. It's not efficient, but it was not possible to add code which would run after all
 * items.
 * </p>
 */
public class UserAppHandler
        implements MetadataTypesHandler<UserApp>, MetadataSearchHandler<UserApp>, MetadataSaveHandler<UserApp>,
        MetadataPropertiesHandler<UserApp> {
    private static final int HIGHER_THEN_BUILD_IN = 10;

    private final Map<Class<? extends UserApp>, String> types;

    private DbSessionFactory sessionFactory;
    private AppFrameworkActivator appFrameworkActivator;

    public UserAppHandler() {
        this(new AppFrameworkActivator());
    }

    public UserAppHandler(final AppFrameworkActivator appFrameworkActivator) {
        this.types = Collections.singletonMap(UserApp.class, "Apps (User)");
        this.appFrameworkActivator = appFrameworkActivator;
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Integer getId(UserApp object) {
        return object.getAppId().hashCode();
    }

    @Override
    public void setId(UserApp object, Integer id) {
        // Nothing to do
    }

    @Override
    public String getUuid(UserApp object) {
        return UUID.nameUUIDFromBytes(object.getAppId().getBytes()).toString();
    }

    @Override
    public void setUuid(UserApp object, String uuid) {
        // Nothing to do
    }

    @Override
    public Boolean getRetired(UserApp object) {
        return Boolean.FALSE;
    }

    @Override
    public void setRetired(UserApp object, Boolean retired) {
        // Nothing to do
    }

    @Override
    public String getName(UserApp object) {
        return object.getAppId();
    }

    @Override
    public String getDescription(UserApp object) {
        return null;
    }

    @Override
    public Date getDateChanged(UserApp object) {
        return null;
    }

    @Override
    public Map<String, Object> getProperties(UserApp object) {
        return Collections.emptyMap();
    }

    @Override
    public UserApp saveItem(UserApp item) {
        validateUserAppId(item.getAppId());

        final UserApp existing = getItemByUuid(UserApp.class, item.getAppId());
        final UserApp saved;

        if (existing == null) {
            saved = item;
        } else {
            existing.setJson(item.getJson());
            saved = existing;
        }

        sessionFactory.getCurrentSession().saveOrUpdate(saved);
        // Inefficient but required
        sessionFactory.getCurrentSession().flush();
        appFrameworkActivator.contextRefreshed();
        return saved;
    }

    @Override
    public int getItemsCount(Class<? extends UserApp> type, boolean includeRetired, String phrase) {
        return ((Number) getCriteriaForAll().setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @Override
    public List<UserApp> getItems(Class<? extends UserApp> type, boolean includeRetired, String phrase, Integer firstResult,
                                  Integer maxResults) {
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
    public UserApp getItemByUuid(Class<? extends UserApp> type, String uuid) {
        final List<UserApp> allUserApps = getCriteriaForAll().list();

        for (UserApp userApp : allUserApps) {
            if (getUuid(userApp).equals(uuid)) {
                return userApp;
            }
        }

        return null;
    }

    @Override
    public UserApp getItemById(Class<? extends UserApp> type, Integer id) {
        final List<UserApp> allUserApp = getCriteriaForAll().list();

        for (final UserApp userApp : allUserApp) {
            if (userApp.getAppId().hashCode() == id) {
                return userApp;
            }
        }

        return null;
    }

    @Override
    public Map<Class<? extends UserApp>, String> getTypes() {
        return types;
    }

    @Override
    public int getPriority() {
        return HIGHER_THEN_BUILD_IN;
    }

    private Criteria getCriteriaForAll() {
        return sessionFactory.getCurrentSession().createCriteria(UserApp.class);
    }

    private void validateUserAppId(String appId) {
        final AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
        final Set<String> allAppsIds =
                appFrameworkService.getAllApps().stream().map(AppDescriptor::getId).collect(Collectors.toSet());
        final Set<String> userApps =
                appFrameworkService.getUserApps().stream().map(UserApp::getAppId).collect(Collectors.toSet());

        // If the ID is in all apps and not in user apps, it means the ID is used by app from source file
        if (allAppsIds.contains(appId) && !userApps.contains(appId)) {
            throw new CflRuntimeException("The user app with ID: " + appId + " could not be added, because there is " +
                    "already an app with such ID and it's not a user app! Built-In apps should be updated by " +
                    "module update. You can remove this app from export package and try again.");
        }
    }
}
