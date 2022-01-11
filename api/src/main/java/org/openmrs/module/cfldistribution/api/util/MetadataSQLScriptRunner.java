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

  public String getQueryFromResource(String resourcesPath) throws IOException {
    String query = null;
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcesPath);
    if (in != null) {
      query = IOUtils.toString(in);
    }
    return query;
  }

  public void executeQueryFromResource(String resourcesPath) throws IOException {
    executeQuery(getQueryFromResource(resourcesPath));
  }
}
