package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.VisitType;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * Adds Visit Types.
 */
public class VisitTypesMetadata extends VersionedMetadataBundle {
    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() {
        // nothing to do
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(newVisitType("Medicine refill", "Medicine refill visit type.", "3627f825-37f2-455b-80e4-86942a409502"));
        install(newVisitType("Sputum collection", "Sputum collection visit type.", "7e0800f0-f3bd-4f64-82e2-266644e3473a"));
        install(newVisitType("Facility Visit",
                "Patient visits the clinic/hospital (as opposed to a home visit, or telephone contact).",
                "7b0f5697-27e3-40c4-8bae-f4049abfb4ed"));
    }

    private VisitType newVisitType(String name, String description, String uuid) {
        final VisitType visitType = new VisitType();
        visitType.setName(name);
        visitType.setDescription(description);
        visitType.setUuid(uuid);
        return visitType;
    }
}
