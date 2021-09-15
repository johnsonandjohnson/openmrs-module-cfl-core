package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * Adds Person Attributes.
 */
public class PersonAttributesMetadata extends VersionedMetadataBundle {
    private PersonService personService;

    @Override
    public int getVersion() {
        return 1;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    protected void installEveryTime() throws Exception {
        // nothing to do
    }

    @Override
    protected void installNewVersion() throws Exception {
        addStringPersonAttributeType("personLanguage", "Attribute used to store information about patient language",
                "ff34db58-c1f4-475b-8bac-30f26e251333");
        addStringPersonAttributeType("dndConsent", "Attribute used to store information about patient dnd consent",
                "a5476c49-32d2-489e-a90b-d08be4b5cff9");
        addStringPersonAttributeType("Gender Identity", "Used on CfL Patient registration form.",
                "603bc838-ed52-4de5-b7c7-57a47de7a2ff");
        addStringPersonAttributeType("Nationality", "Used on CfL Patient registration form.",
                "1cf8b660-65a7-4335-b56d-672722182bc2");
        addStringPersonAttributeType("City of current residence", "Used on CfL Patient registration form.",
                "66119d21-4eda-466a-ac19-cadab568cf25");
        addStringPersonAttributeType("City of origin", "Used on CfL Patient registration form.",
                "03acac42-9c29-4007-9253-5226ac3e2b6d");
        addStringPersonAttributeType("Civil status", "Used on CfL Patient registration form.",
                "8d4a881a-e2fd-495c-b04f-d73f8a6e6146");
        addStringPersonAttributeType("Education degree", "Used on CfL Patient registration form.",
                "af032e9f-721e-4d2d-833a-5fc452e79e05");
        addStringPersonAttributeType("Sector", "Used on CfL Patient registration form.",
                "83208670-f43c-476f-a519-10a131d03c2d");
        addStringPersonAttributeType("Job", "Used on CfL Patient registration form.",
                "8dc7d8a5-3061-4894-8651-a7d35da3a8aa");

    }

    private void addStringPersonAttributeType(String name, String description, String uuid) {
        final PersonAttributeType type = new PersonAttributeType();
        type.setFormat(String.class.getName());
        type.setName(name);
        type.setDescription(description);
        type.setUuid(uuid);
        personService.savePersonAttributeType(type);
    }
}
