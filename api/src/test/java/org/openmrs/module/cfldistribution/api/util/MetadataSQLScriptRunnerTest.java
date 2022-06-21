package org.openmrs.module.cfldistribution.api.util;

import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSessionFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MetadataSQLScriptRunnerTest {

  private static final String RESOURCE_PATH = "testQuery.sql";

  private DbSessionFactory dbSessionFactory;

  @Test
  public void shouldGetSqlContentFromFileAsText() throws IOException {
    MetadataSQLScriptRunner runner = new MetadataSQLScriptRunner(dbSessionFactory);

    String actual = runner.getQueryFromResource(RESOURCE_PATH);

    assertNotNull(actual);
    assertEquals("SELECT 1;", actual);
  }
}
