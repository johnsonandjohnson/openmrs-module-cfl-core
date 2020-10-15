package org.openmrs.module.cfl.api.service.impl;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;

import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_KEY;

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
    public Vaccination[] getRandomizationGlobalProperty() {
        return new Gson().fromJson(getGp(VACCINATION_PROGRAM_KEY), Vaccination[].class);
    }

    @Override
    public String getVaccinationProgram(Person person) {
        return person.getAttribute(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME).getValue();
    }

    @Override
    public boolean isVaccinationInfoIsEnabled() {
        return Boolean.parseBoolean(Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY));
    }

    @Override
    public String getRefreshDate(Person person) {
        return person.getAttribute(CFLConstants.REFRESH_DATE_ATTRIBUTE_NAME).getValue();
    }

    @Override
    public String getLastVisitRefreshDate() {
        return getGp(CFLConstants.ZETES_LAST_VISIT_REFRESH_DATE);
    }

    @Override
    public void setLastVisitRefreshDate(String value) {
      setGp(CFLConstants.ZETES_LAST_VISIT_REFRESH_DATE, value);
    }

    @Override
    public String getLastPatientRefreshDate() {
        return getGp(CFLConstants.ZETES_LAST_PATIENT_REFRESH_DATE);
    }

    @Override
    public void setLastPatientRefreshDate(String value) {
        setGp(CFLConstants.ZETES_LAST_PATIENT_REFRESH_DATE, value);
    }

    private void setGp(String propertyName, String value) {
      Context.getAdministrationService().setGlobalProperty(propertyName, value);
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }

    private String getGp(String propertyName, String defaultValue) {
        return Context.getAdministrationService().getGlobalProperty(propertyName, defaultValue);
    }
}
