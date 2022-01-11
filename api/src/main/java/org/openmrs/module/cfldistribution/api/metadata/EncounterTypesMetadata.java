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
    install(newEncounterType("CFL HIV Intake Visit", "c1c99d40-3039-4ff2-8213-fdf4b5ece4d5"));
    install(newEncounterType("CFL HIV Program Enrollment", "645ce7ac-3714-4cb4-922a-9d5b4b164fa8"));
    install(newEncounterType("CFL HIV Visit", "6932803d-f0a3-44e5-90cd-e08d86f98d70"));
    install(newEncounterType("Symptoms", "43c3630f-abfe-4fe1-8c92-b73b65199a3d"));
  }

  private EncounterType newEncounterType(String name, String uuid) {
    final EncounterType encounterType = new EncounterType();
    encounterType.setName(name);
    encounterType.setUuid(uuid);
    return encounterType;
  }
}
