package org.openmrs.module.cfldistribution.api.metadata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.OrderFrequency;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class OrderFrequencyMetadataTest {

  @Mock private ConceptService conceptService;

  @Mock private MetadataDeployService metadataDeployService;

  @InjectMocks private OrderFrequencyMetadata orderFrequencyMetadata;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getConceptService()).thenReturn(conceptService);
  }

  @Test
  public void shouldReturnProperVersion() {
    int actual = orderFrequencyMetadata.getVersion();

    assertEquals(2, actual);
  }

  @Test
  public void shouldCreateOrderFrequencyConfig() {
    orderFrequencyMetadata.installNewVersion();

    verify(conceptService, times(30)).getConceptByUuid(anyString());
    verify(metadataDeployService, times(30)).installObject(any(OrderFrequency.class));
  }
}
