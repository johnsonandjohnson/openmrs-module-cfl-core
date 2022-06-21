package org.openmrs.module.cfldistribution.api.metadata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class AdminProviderMetadataTest {

  @Mock private ProviderService providerService;

  @Mock private PersonService personService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getProviderService()).thenReturn(providerService);
    when(Context.getPersonService()).thenReturn(personService);
    when(providerService.getProvidersByPerson(any(Person.class)))
        .thenReturn(Collections.emptyList());
  }

  @Test
  public void createAdminProviderIfDoesNotExist() {
    when(providerService.getAllProviders(false)).thenReturn(Collections.emptyList());

    new AdminProviderMetadata().installNewVersion();

    verify(providerService).saveProvider(any(Provider.class));
  }

  @Test
  public void updateAdminProviderIfAlreadyExists() {
    when(providerService.getAllProviders(false))
        .thenReturn(Collections.singletonList(new Provider(1)));

    new AdminProviderMetadata().installNewVersion();

    verify(providerService).saveProvider(any(Provider.class));
  }
}
