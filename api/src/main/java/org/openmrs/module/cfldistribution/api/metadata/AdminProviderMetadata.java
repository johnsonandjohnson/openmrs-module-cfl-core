package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

import java.util.Collection;
import java.util.List;

/** Adds Provider account to the Super User (admin). */
public class AdminProviderMetadata extends VersionedMetadataBundle {
  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  protected void installEveryTime() {}

  @Override
  protected void installNewVersion() {
    final ProviderService providerService = Context.getProviderService();
    final Person adminPerson = Context.getPersonService().getPerson(1);
    final Collection<Provider> possibleProviders =
        providerService.getProvidersByPerson(adminPerson);

    if (possibleProviders.isEmpty()) {
      final List<Provider> providers = providerService.getAllProviders(false);

      final Provider provider;
      if (providers.isEmpty()) {
        provider = new Provider();
        provider.setIdentifier("admin");
      } else {
        provider = providers.get(0);
      }
      provider.setPerson(adminPerson);
      providerService.saveProvider(provider);
    }
  }
}
