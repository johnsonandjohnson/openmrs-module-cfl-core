package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.EncounterType;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/** Adds Encounter Types. */
public class EncounterTypesMetadata extends VersionedMetadataBundle {
  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  protected void installEveryTime() throws Exception {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws Exception {
    install(newEncounterType("CFL Discontinue program", "f1c23f25-20e1-4503-b09e-116aba0a6063"));
  }

  private EncounterType newEncounterType(String name, String uuid) {
    final EncounterType encounterType = new EncounterType();
    encounterType.setName(name);
    encounterType.setUuid(uuid);
    return encounterType;
  }
}
