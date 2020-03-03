package org.openmrs.module.cfl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleException;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.event.AbstractMessagesEventListener;
import org.openmrs.module.cfl.api.event.CflEventListenerFactory;
import org.openmrs.module.cfl.api.util.AppFrameworkConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CFLModuleActivator extends BaseModuleActivator implements DaemonTokenAware {

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * @see #started()
     */
    @Override
    public void started() {
        log.info("Started CFL Module");
        try {
            setupHtmlForms();
            createPersonOverviewConfig();
            createGlobalSettings();
            createPersonAttributeTypes();
            createHtmlFormProperties();
            configureDistribution();
            installMetadataPackages();
            CflEventListenerFactory.registerEventListeners();
        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(CFLConstants.MODULE_ID);
            ModuleFactory.stopModule(mod);
            throw new ModuleException("failed to setup the required modules", e);
        }
    }

    /**
     * @see #shutdown()
     */
    public void shutdown() {
        log.info("Shutdown CFL Module");
        CflEventListenerFactory.unRegisterEventListeners();
    }

    /**
     * @see #stopped()
     */
    @Override
    public void stopped() {
        log.info("Stopped CFL Module");
        CflEventListenerFactory.unRegisterEventListeners();
    }

    @Override
    public void setDaemonToken(DaemonToken token) {
        log.info("Set daemon token to CFL Module event listeners");
        List<AbstractMessagesEventListener> eventComponents =
                Context.getRegisteredComponents(AbstractMessagesEventListener.class);
        for (AbstractMessagesEventListener eventListener : eventComponents) {
            eventListener.setDaemonToken(token);
        }
    }

    private void createPersonAttributeTypes() {
        createPersonIdentifierAttributeType();
    }

    /**
     * Creates person attribute type used to store information about additional person identifier.
     */
    private void createPersonIdentifierAttributeType() {
        PersonAttributeType attributeType = new PersonAttributeType();
        attributeType.setName(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_NAME);
        attributeType.setFormat(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_FORMAT);
        attributeType.setDescription(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_DESCRIPTION);
        attributeType.setUuid(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_UUID);
        createPersonAttributeTypeIfNotExists(attributeType);
    }

    private void createPersonAttributeTypeIfNotExists(PersonAttributeType attributeType) {
        PersonService personService = Context.getPersonService();
        PersonAttributeType actual = personService.getPersonAttributeTypeByUuid(attributeType.getUuid());
        if (actual == null) {
            personService.savePersonAttributeType(attributeType);
        }
    }

    private void createGlobalSettings() {
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
                CFLConstants.PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE,
                CFLConstants.PATIENT_DASHBOARD_REDIRECT_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME,
                CFLConstants.LOCATION_ATTRIBUTE_TYPE_UUID);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.DISABLED_CONTROL_KEY,
                CFLConstants.DISABLED_CONTROL_DEFAULT_VALUE,
                CFLConstants.DISABLED_CONTROL_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_KEY,
                CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE,
                CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.SUPPORTED_ACTOR_TYPE,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY,
                CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE,
                CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY,
                CFLConstants.PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE,
                CFLConstants.PERSON_IDENTIFIER_SOURCE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_KEY,
                CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE,
                CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION);
    }

    private void configureDistribution() {
        AdministrationService administrationService = Context.getService(AdministrationService.class);
        AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
        disableUnusedExtensions(appFrameworkService);
        if (CFLConstants.TRUE.equalsIgnoreCase(
                administrationService.getGlobalProperty(CFLConstants.DISABLED_CONTROL_KEY))) {
            enableAdditionalConfiguration(appFrameworkService);
        } else {
            enableDefaultConfiguration(appFrameworkService);
        }
    }

    private void disableUnusedExtensions(AppFrameworkService appFrameworkService) {
        disableExtensions(appFrameworkService,
                Collections.singletonList(AppFrameworkConstants.REGISTRATION_APP_EDIT_PATIENT_DASHBOARD_EXT));
    }

    private void enableAdditionalConfiguration(AppFrameworkService appFrameworkService) {
        enableApps(appFrameworkService, AppFrameworkConstants.CFL_ADDITIONAL_MODIFICATION_APP_IDS);
        disableApps(appFrameworkService, AppFrameworkConstants.APP_IDS);
        disableExtensions(appFrameworkService, AppFrameworkConstants.EXTENSION_IDS);
    }

    private void enableDefaultConfiguration(AppFrameworkService appFrameworkService) {
        disableApps(appFrameworkService, AppFrameworkConstants.CFL_ADDITIONAL_MODIFICATION_APP_IDS);
    }

    private void enableApps(AppFrameworkService service, List<String> appIds) {
        for (String app : appIds) {
            service.enableApp(app);
        }
    }

    private void disableApps(AppFrameworkService service, List<String> appIds) {
        for (String app : appIds) {
            service.disableApp(app);
        }
    }

    private void disableExtensions(AppFrameworkService service, List<String> extensions) {
        for (String ext : extensions) {
            service.disableExtension(ext);
        }
    }

    private void setupHtmlForms() throws IOException {
        ResourceFactory resourceFactory = ResourceFactory.getInstance();
        FormService formService = Context.getFormService();
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

        List<String> htmlforms = Arrays.asList(
                "cfl:htmlforms/cfl-HIV.xml",
                "cfl:htmlforms/cfl-check-in.xml",
                "cfl:htmlforms/cfl-visit-note.xml"
        );

        for (String htmlform : htmlforms) {
            HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
        }
    }

    private void installMetadataPackages() {
        try {
            MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
        } catch (Exception e) {
            throw new ModuleException("Failed to load Metadata sharing packages", e);
        }
    }

    private void createPersonOverviewConfig() {
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY,
                ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE,
                ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DESCRIPTION);

        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY,
                ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE,
                ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DESCRIPTION);
    }

    private void createHtmlFormProperties() {
        GlobalPropertyUtils.createGlobalSettingIfNotExists(
                CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_KEY,
                CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE,
                CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION);
    }
}
