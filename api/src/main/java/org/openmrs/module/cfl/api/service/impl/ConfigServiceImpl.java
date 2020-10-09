package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;

public class ConfigServiceImpl implements ConfigService {

    @Override
    public String getActorTypesConfiguration() {
        return getGp(CFLConstants.ACTOR_TYPES_KEY);
    }

    @Override
    public String getDefaultActorRelationDirection() {
        return RelationshipTypeDirection.A.name();
    }

    @Override
    public FindPersonFilterStrategy getPersonFilterStrategy() {
        String beanName = getGp(ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY);
        if (StringUtils.isBlank(beanName)) {
            return null;
        }
        return Context.getRegisteredComponent(beanName, FindPersonFilterStrategy.class);
    }

    @Override
    public int getLastViewedPersonSizeLimit() {
        String gpName = ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY;
        return GlobalPropertyUtils.parseInt(
                gpName,
                getGp(gpName, ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE));
    }

    @Override
    public Vaccination getVaccinationGlobalProperty() {
        return null;
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }

    private String getGp(String propertyName, String defaultValue) {
        return Context.getAdministrationService().getGlobalProperty(propertyName, defaultValue);
    }
}
