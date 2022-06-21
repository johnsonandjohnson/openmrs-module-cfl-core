package org.openmrs.module.cfldistribution.api.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlFormsMetadataTest {

  @Test
  public void shouldReturnProperVersion() {
    HtmlFormsMetadata htmlFormsMetadata = new HtmlFormsMetadata();

    int actual = htmlFormsMetadata.getVersion();

    assertEquals(7, actual);
  }
}
