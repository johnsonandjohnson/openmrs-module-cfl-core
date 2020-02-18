package org.openmrs.module.cfl.api.copied.messages.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cfl.api.copied.messages.service.ConfigService;

public class ConfigServiceImpl implements ConfigService {

    @Override
    public String getActorTypesConfiguration() {
        return getGp(CFLConstants.ACTOR_TYPES_KEY);
    }

    @Override
    public String getDefaultActorRelationDirection() {
        return RelationshipTypeDirection.A.name();
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }
}
