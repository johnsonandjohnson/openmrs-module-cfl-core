package org.openmrs.module.cfl.handler.metadatasharing;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appframework.AppFrameworkActivator;
import org.openmrs.module.appframework.domain.UserApp;
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
 * The UserAppHandler exposes {@link UserApp} entity to Metadata Sharing module for export and import.
 * <p>
 * This bean is configured in moduleApplicationContext.xml
 * </p>
 * <p>
 * The {@link UserApp} has no ID nor UUID, for both the String `appId` is used: its hashcode for Integer ID and its
 * value for UUID (leveraging the lack of UUID validation).
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
    final Map<Class<? extends UserApp>, String> tmpTypes = new HashMap<Class<? extends UserApp>, String>();
    tmpTypes.put(UserApp.class, "Apps (User)");
    this.types = Collections.unmodifiableMap(tmpTypes);
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
    return object.getAppId();
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
    final UserApp saved;

    final UserApp existing = getItemByUuid(UserApp.class, item.getAppId());

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

  private Criteria getCriteriaForAll() {
    return sessionFactory.getCurrentSession().createCriteria(UserApp.class);
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
    return (UserApp) getCriteriaForAll().add(Restrictions.eq("appId", uuid)).uniqueResult();
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
}
