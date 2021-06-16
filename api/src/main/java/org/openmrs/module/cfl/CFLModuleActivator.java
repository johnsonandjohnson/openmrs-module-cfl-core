package org.openmrs.module.cfl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleException;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.constant.RolePrivilegeConstants;
import org.openmrs.module.cfl.api.event.AbstractMessagesEventListener;
import org.openmrs.module.cfl.api.event.CflEventListenerFactory;
import org.openmrs.module.cfl.api.event.listener.subscribable.BaseActionListener;
import org.openmrs.module.cfl.api.util.AppFrameworkConstants;
import org.openmrs.module.cfl.api.util.DataCleanup;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
            attachProgramsManagingPrivilegesToSuperUser();
            ensureCorrectRolesAreAssignedToAdmin();
            fixRiskFactorForHIVConcepts();
            setupHtmlForms();
            createPersonOverviewConfig();
            createGlobalSettings();
            createPersonAttributeTypes();
            createHtmlFormProperties();
            createVisitNoteUrlProperties();
            configureDistribution();
            installMetadataPackages();
            CflEventListenerFactory.registerEventListeners();
            deployMetadataPackages();
            ensureCFLTagRoleConfigurationIncludesDoctorRole();
            DataCleanup.cleanUpUnnecessaryData();
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

        List<BaseActionListener> actionListeners = Context.getRegisteredComponents(
                BaseActionListener.class);
        for (BaseActionListener actionListener : actionListeners) {
            actionListener.setDaemonToken(token);
        }
    }

    private void attachProgramsManagingPrivilegesToSuperUser() {
        Role superUserRole = Context.getUserService().getRole(CFLConstants.SUPER_USER_ROLE_NAME);
        if (superUserRole == null) {
            log.warn(String.format("Cannot find the super user role (%s)", CFLConstants.SUPER_USER_ROLE_NAME));
            return;
        }
        attachMissingPrivileges(CFLConstants.PROGRAM_MANAGING_PRIVILEGES_NAMES, superUserRole);
    }

    private void ensureCorrectRolesAreAssignedToAdmin() {
        final UserService userService = Context.getUserService();
        final User adminUser = userService.getUserByUsername(CFLConstants.ADMIN_USER_NAME);
        final Role fullPrivilegeRole = userService.getRole(CFLConstants.PRIVILEGE_LEVEL_FULL_ROLE_NAME);

        adminUser.addRole(fullPrivilegeRole);
    }

    private void ensureCFLTagRoleConfigurationIncludesDoctorRole() {
        final FlagService flagService = Context.getService(FlagService.class);

        final Tag cflTag = flagService.getTagByUuid(CFLConstants.CFL_TAG_UUID);
        final Role doctorRole = Context.getUserService().getRole(RolePrivilegeConstants.DOCTOR_PRIVILEGE_LEVEL);

        for (final Role cflTagRole : cflTag.getRoles()) {
            if (cflTagRole.equals(doctorRole)) {
                // The configuration is ok
                return;
            }
        }

        // The configuration is not ok
        cflTag.getRoles().add(doctorRole);
        flagService.saveTag(cflTag);
    }

    /**
     * Fix 'Unknown' and 'Other' Answers which are added by liquibase migration before the relevant Concepts are present in
     * the system.
     */
    private void fixRiskFactorForHIVConcepts() {
        final String riskFactorConceptUuid = "f7c6f4f7-89d2-48f9-8844-c1a6564231c0";
        fixMissingConceptAnswer("Risk factor for HIV", riskFactorConceptUuid, "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        final String highRiskGroupsConceptUuid = "9994a4aa-981b-4605-8f4f-23e02e64106e";
        fixMissingConceptAnswer("High Risk Groups", highRiskGroupsConceptUuid, "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    private void fixMissingConceptAnswer(final String conceptName, final String conceptUuid,
                                         final String missingAnswerUuid) {
        final ConceptService conceptService = Context.getConceptService();
        final Concept concept = conceptService.getConceptByUuid(conceptUuid);

        if (concept == null) {
            log.error(new StringBuilder("Concept '")
                    .append(conceptName)
                    .append("' for which to fix an Answer was not found by UUID: ")
                    .append(conceptUuid)
                    .append(". System may be unstable!")
                    .toString());
        } else {
            // We expect only one Answer to be missing
            for (final ConceptAnswer conceptAnswer : concept.getAnswers()) {
                if (conceptAnswer.getAnswerConcept() == null) {
                    conceptAnswer.setAnswerConcept(conceptService.getConceptByUuid(missingAnswerUuid));
                    return;
                }
            }
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
        attributeType.setSearchable(true);
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
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
                CFLConstants.PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE, CFLConstants.PATIENT_DASHBOARD_REDIRECT_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME,
                CFLConstants.LOCATION_ATTRIBUTE_TYPE_UUID);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.DISABLED_CONTROL_KEY,
                CFLConstants.DISABLED_CONTROL_DEFAULT_VALUE, CFLConstants.DISABLED_CONTROL_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_KEY,
                CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE,
                CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.SUPPORTED_ACTOR_TYPE,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE, CFLConstants.SUPPORTED_ACTOR_TYPE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE,
                CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY,
                CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE,
                CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY,
                CFLConstants.PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE, CFLConstants.PERSON_IDENTIFIER_SOURCE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_KEY,
                CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE,
                CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY,
                CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE, CFLConstants.PERSON_LOCATION_ATTRIBUTE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.CONDITION_LIST_CLASSES_KEY,
                CFLConstants.CONDITION_LIST_CLASSES_DEFAULT_VALUE, CFLConstants.CONDITION_LIST_CLASSES_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY,
                CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DEFAULT_VALUE,
                CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.VACCINATION_PROGRAM_KEY,
                CFLConstants.VACCINATION_PROGRAM_DEFAULT_VALUE, CFLConstants.VACCINATION_PROGRAM_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY,
                CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DEFAULT_VALUE,
                CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.COUNTRY_SETTINGS_MAP_KEY,
                CFLConstants.COUNTRY_SETTINGS_MAP_DEFAULT_VALUE, CFLConstants.COUNTRY_SETTINGS_MAP_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.ACTOR_TYPES_KEY,
                CFLConstants.CAREGIVER_RELATIONSHIP_UUID);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE,
                CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_VALUE,
                CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY,
                CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DEFAULT_VALUE,
                CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY,
                CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DEFAULT_VALUE,
                CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DESCRIPTION);
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.VACCINATION_LISTENER_KEY,
                CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME,
                CFLConstants.VACCINATION_LISTENER_DESCRIPTION);
    }

    private void createVisitNoteUrlProperties() {
        GlobalPropertyUtils.createGlobalSettingIfNotExists(GlobalPropertiesConstants.VISIT_FORM_URIS);
    }

    private void configureDistribution() {
        AdministrationService administrationService = Context.getService(AdministrationService.class);
        AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
        disableUnusedExtensions(appFrameworkService);
        if (CFLConstants.TRUE.equalsIgnoreCase(administrationService.getGlobalProperty(CFLConstants.DISABLED_CONTROL_KEY))) {
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

        List<String> htmlforms = Arrays.asList("cfl:htmlforms/cfl-HIV.xml", "cfl:htmlforms/cfl-check-in.xml",
                "cfl:htmlforms/cfl-visit-note.xml", "cfl:htmlforms/cfl-medicine-refill.xml",
                "cfl:htmlforms/cfl-sputum-visit-note.xml", "cfl:htmlforms/encounters-form.xml",
                "cfl:htmlforms/cfl-hiv-enrollment.xml", "cfl:htmlforms/discontinue-program.xml");

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
        GlobalPropertyUtils.createGlobalSettingIfNotExists(ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY,
                ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE,
                ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DESCRIPTION);

        GlobalPropertyUtils.createGlobalSettingIfNotExists(ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY,
                ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE,
                ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DESCRIPTION);
    }

    private void createHtmlFormProperties() {
        GlobalPropertyUtils.createGlobalSettingIfNotExists(CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_KEY,
                CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE,
                CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION);
    }

    private void attachMissingPrivileges(List<String> privilegeNames, Role role) {
        Set<Privilege> privileges = role.getPrivileges();
        UserService userService = Context.getUserService();
        boolean updated = false;
        for (String privilegeName : privilegeNames) {
            Privilege privilege = userService.getPrivilege(privilegeName);
            if (privilege == null) {
                log.warn(String.format(
                        "Cannot find the privilege %s, " + "so it will not be attached to the role %s at the startup",
                        privilegeName, role.getName()));
            } else if (!privileges.contains(privilege)) {
                log.info(String.format("Attached the privilege %s to the role %s", privilege.getName(), role.getName()));
                role.addPrivilege(privilege);
                updated = true;
            }
        }
        if (updated) {
            userService.saveRole(role);
        }
    }

    private void deployMetadataPackages() {
        MetadataDeployService service = Context.getRegisteredComponent("metadataDeployService", MetadataDeployService.class);
        MetadataBundle rolesAndPrivileges = Context.getRegisteredComponent("cflRolePrivilegeProfiles", MetadataBundle.class);
        service.installBundles(Collections.singletonList(rolesAndPrivileges));
    }
}
