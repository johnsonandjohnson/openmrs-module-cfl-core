package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/** Adds Programs. */
public class ProgramsMetadata extends VersionedMetadataBundle {
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
    install(newHIVProgram());
  }

  private Program newHIVProgram() {
    final Concept hivConcept = Context.getConceptService().getConceptByName("HIV");
    final Program hivProgram = new Program();
    // Hardcoded because we reference it in HTML forms
    hivProgram.setUuid("06c596e7-53dd-44c1-9609-f1406fd9e76d");
    hivProgram.setName("HIV");
    hivProgram.setConcept(hivConcept);
    return hivProgram;
  }
}
