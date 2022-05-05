package org.openmrs.module.cfldistribution.api.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.openmrs.api.db.hibernate.DbSessionFactory;

import java.io.IOException;
import java.io.InputStream;

/** The utility class to read SQL scripts from classpath and execute them. */
public class MetadataSQLScriptRunner {
  private final DbSessionFactory dbSessionFactory;

  public MetadataSQLScriptRunner(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  public void executeQuery(String query) {
    if (StringUtils.isNotBlank(query)) {
      SQLQuery sqlQuery = dbSessionFactory.getCurrentSession().createSQLQuery(query);
      sqlQuery.executeUpdate();
    }
  }

  /**
   * Get textual content of resource under {@code resourcesPath}.
   * <b>The class loader of this class will be used to obtain a resource.</b>
   *
   * @param resourcesPath the resource path, not null
   * @return the resource content, null if empty
   * @throws IOException if there was error during reading a resource
   */
  public String getQueryFromResource(String resourcesPath) throws IOException {
    return getQueryFromResource(resourcesPath, this.getClass().getClassLoader());
  }

  /**
   * Get textual content of resource under {@code resourcesPath}.
   * <b>The class loader of this class will be used to obtain a resource.</b>
   *
   * @param resourcesPath the resource path, not null
   * @param classLoader the ClassLoader to use when searching for the resource, not null
   * @return the resource content, null if empty
   * @throws IOException if there was error during reading a resource
   */
  public String getQueryFromResource(String resourcesPath, ClassLoader classLoader)
      throws IOException {
    String query = null;
    InputStream in = classLoader.getResourceAsStream(resourcesPath);
    if (in != null) {
      query = IOUtils.toString(in);
    }
    return query;
  }

  public void executeQueryFromResource(String resourcesPath) throws IOException {
    executeQuery(getQueryFromResource(resourcesPath));
  }
}
