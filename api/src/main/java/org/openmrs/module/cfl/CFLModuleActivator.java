package org.openmrs.module.cfl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.ModuleException;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.event.CflEventListenerHelper;
import org.openmrs.module.cfl.api.event.listener.subscribable.BaseListener;
import org.openmrs.module.cfl.api.htmlformentry.tag.RegimenHandler;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openmrs.api.context.Context.getRegisteredComponent;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.CALL_CONFIG_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.CALL_CONFIG_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SEND_SMS_ON_PATIENT_REGISTRATION_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_CREATE_FIRST_VISIT_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_CREATE_FIRST_VISIT_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_CALL_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_CALL_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_SMS_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_SMS_PROP_NAME;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SMS_CONFIG_PROP_DESC;
import static org.openmrs.module.cfl.api.constant.CountryPropertyConstants.SMS_CONFIG_PROP_NAME;
import static org.openmrs.module.cfl.api.util.GlobalPropertyUtils.createGlobalSettingIfNotExists;
import static org.openmrs.module.messages.api.util.CountryPropertyUtils.createDefaultCountrySettingIfNotExists;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CFLModuleActivator extends BaseModuleActivator implements DaemonTokenAware {

  private Log log = LogFactory.getLog(this.getClass());

  /** @see #started() */
  @Override
  public void started() {
    log.info("Started CFL Module");
    try {
      attachProgramsManagingPrivilegesToSuperUser();
      ensureCorrectRolesAreAssignedToAdmin();

      // These 3 are Global Properties
      createPersonOverviewConfig();
      createGlobalSettings();
      createCountrySettings();

      CflEventListenerHelper.registerEventListeners();

      createPersonAttributeTypes();

      // Install metadata packages
      MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
      installMetadataBundles();

      addTagHandlers();
    } catch (Exception e) {
      throw new ModuleException("failed to setup the required modules", e);
    }
  }

  /** @see #stopped() */
  @Override
  public void stopped() {
    log.info("Stopped CFL Module");
    CflEventListenerHelper.unRegisterEventListeners();
  }

  @Override
  public void setDaemonToken(DaemonToken token) {
    log.info("Set daemon token to CFL Module event listeners");
    final List<BaseListener> listeners = Context.getRegisteredComponents(BaseListener.class);
    for (BaseListener listener : listeners) {
      listener.setDaemonToken(token);
    }
  }

  private void attachProgramsManagingPrivilegesToSuperUser() {
    Role superUserRole = Context.getUserService().getRole(CFLConstants.SUPER_USER_ROLE_NAME);
    if (superUserRole == null) {
      log.warn(
          String.format("Cannot find the super user role (%s)", CFLConstants.SUPER_USER_ROLE_NAME));
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

  private void createPersonAttributeTypes() {
    createPersonIdentifierAttributeType();
  }

  /** Creates person attribute type used to store information about additional person identifier. */
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
    final PersonService personService = Context.getPersonService();

    PersonAttributeType actual =
        personService.getPersonAttributeTypeByUuid(attributeType.getUuid());
    if (actual != null) {
      return;
    }

    actual = personService.getPersonAttributeTypeByName(attributeType.getName());

    if (actual != null) {
      log.warn(
          "The Person Attribute Type '"
              + attributeType.getName()
              + "' already exists, but have different "
              + "UUID then expected by CFL (has: "
              + actual.getUuid()
              + ", expected: "
              + attributeType.getUuid()
              + ")!");
    } else {
      personService.savePersonAttributeType(attributeType);
    }
  }

  private void createGlobalSettings() {
    createGlobalSettingIfNotExists(
        CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
        CFLConstants.PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE,
        CFLConstants.PATIENT_DASHBOARD_REDIRECT_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_KEY,
        CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE,
        CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.SUPPORTED_ACTOR_TYPE,
        CFLConstants.SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE,
        CFLConstants.SUPPORTED_ACTOR_TYPE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION,
        CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE,
        CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY,
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE,
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY,
        CFLConstants.PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE,
        CFLConstants.PERSON_IDENTIFIER_SOURCE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_KEY,
        CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE,
        CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY,
        CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE,
        CFLConstants.PERSON_LOCATION_ATTRIBUTE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.CONDITION_LIST_CLASSES_KEY,
        CFLConstants.CONDITION_LIST_CLASSES_DEFAULT_VALUE,
        CFLConstants.CONDITION_LIST_CLASSES_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY,
        CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DEFAULT_VALUE,
        CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.VACCINATION_PROGRAM_KEY,
        CFLConstants.VACCINATION_PROGRAM_DEFAULT_VALUE,
        CFLConstants.VACCINATION_PROGRAM_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY,
        CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DEFAULT_VALUE,
        CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.ACTOR_TYPES_KEY, CFLConstants.CAREGIVER_RELATIONSHIP_UUID);
    createGlobalSettingIfNotExists(
        CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE,
        CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_VALUE,
        CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY,
        CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DEFAULT_VALUE,
        CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY,
        CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DEFAULT_VALUE,
        CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.VACCINATION_LISTENER_KEY,
        CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME,
        CFLConstants.VACCINATION_LISTENER_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_KEY,
        CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE,
        CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION);
    createGlobalSettingIfNotExists(
        CFLConstants.ENVIRONMENT_KEY,
        CFLConstants.ENVIRONMENT_DEFAULT_VALUE,
        CFLConstants.ENVIRONMENT_DESCRIPTION);
    createGlobalSettingIfNotExists(GlobalPropertiesConstants.VISIT_FORM_URIS);
    createGlobalSettingIfNotExists(
        GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID);
    createGlobalSettingIfNotExists(
        GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID);
  }

  private void createCountrySettings() {
    createDefaultCountrySettingIfNotExists(
        PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME,
        "10:00",
        PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME,
        "18:00",
        PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SHOULD_SEND_REMINDER_VIA_SMS_PROP_NAME,
        Boolean.FALSE.toString(),
        SHOULD_SEND_REMINDER_VIA_SMS_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME,
        Boolean.FALSE.toString(),
        PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SMS_CONFIG_PROP_NAME, "defaultSmS", SMS_CONFIG_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        CALL_CONFIG_PROP_NAME, "defaultCall", CALL_CONFIG_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME,
        Boolean.FALSE.toString(),
        SEND_SMS_ON_PATIENT_REGISTRATION_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SHOULD_SEND_REMINDER_VIA_CALL_PROP_NAME,
        Boolean.FALSE.toString(),
        SHOULD_SEND_REMINDER_VIA_CALL_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SHOULD_CREATE_FIRST_VISIT_PROP_NAME,
        Boolean.FALSE.toString(),
        SHOULD_CREATE_FIRST_VISIT_PROP_DESC);
    createDefaultCountrySettingIfNotExists(
        SHOULD_CREATE_FUTURE_VISIT_PROP_NAME,
        Boolean.FALSE.toString(),
        SHOULD_CREATE_FUTURE_VISIT_PROP_DESC);
  }

  private void createPersonOverviewConfig() {
    createGlobalSettingIfNotExists(
        ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY,
        ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE,
        ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DESCRIPTION);

    createGlobalSettingIfNotExists(
        ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY,
        ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE,
        ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DESCRIPTION);
  }

  private void attachMissingPrivileges(List<String> privilegeNames, Role role) {
    Set<Privilege> privileges = role.getPrivileges();
    UserService userService = Context.getUserService();
    boolean updated = false;
    for (String privilegeName : privilegeNames) {
      Privilege privilege = userService.getPrivilege(privilegeName);
      if (privilege == null) {
        log.warn(
            String.format(
                "Cannot find the privilege %s, so it will not be attached to the role %s at the startup",
                privilegeName, role.getName()));
      } else if (!privileges.contains(privilege)) {
        log.info(
            String.format(
                "Attached the privilege %s to the role %s", privilege.getName(), role.getName()));
        role.addPrivilege(privilege);
        updated = true;
      }
    }
    if (updated) {
      userService.saveRole(role);
    }
  }

  private void installMetadataBundles() {
    final MetadataDeployService service =
        getRegisteredComponent("metadataDeployService", MetadataDeployService.class);
    final List<MetadataBundle> cflBundles =
        Context.getRegisteredComponents(MetadataBundle.class).stream()
            .filter(
                component ->
                    component.getClass().getName().startsWith(CFLConstants.MODULE_API_PACKAGE))
            .collect(Collectors.toList());

    service.installBundles(cflBundles);
  }

  private void addTagHandlers() {
    Context.getService(HtmlFormEntryService.class)
        .addHandler(CFLConstants.REGIMEN_TAG_NAME, new RegimenHandler());
  }
}
