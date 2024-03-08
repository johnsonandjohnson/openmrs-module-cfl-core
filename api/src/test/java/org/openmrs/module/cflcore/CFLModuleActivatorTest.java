/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.MockMetadataBundle;
import org.openmrs.module.cflcore.api.constant.ConfigConstants;
import org.openmrs.module.cflcore.api.event.CflEventListenerHelper;
import org.openmrs.module.cflcore.api.util.GPDefinition;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cflcore.api.util.GlobalPropertyUtils;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.handler.TagHandler;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.model.CountryProperty;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.openmrs.module.messages.api.service.MessagesSchedulerService;
import org.openmrs.module.messages.api.util.CountryPropertyUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  Context.class,
  CflEventListenerHelper.class,
  MetadataUtil.class,
  GlobalPropertyUtils.class,
  CountryPropertyUtils.class
})
public class CFLModuleActivatorTest {

  @Mock private UserService userService;
  @Mock private AdministrationService administrationService;
  @Mock private CountryPropertyService countryPropertyService;
  @Mock private PersonService personService;
  @Mock private MetadataDeployService metadataDeployService;
  private MetadataBundle mockMetadataBundle = new MockMetadataBundle();
  @Mock private HtmlFormEntryService htmlFormEntryService;
  @Mock private MessagesSchedulerService messagesSchedulerService;

  private Role superUserRole = new Role();
  private Role fullPrivilegeRole = new Role();
  private User adminUser = new User();
  private Set<GlobalProperty> createdGlobalProperties = new HashSet<>();
  private Set<CountryProperty> createdCountryProperties = new HashSet<>();

  @Before
  public void setupContext() throws Exception {
    superUserRole.setPrivileges(new HashSet<>());

    PowerMockito.mockStatic(
        Context.class,
        CflEventListenerHelper.class,
        MetadataUtil.class,
        GlobalPropertyUtils.class,
        CountryPropertyUtils.class);

    PowerMockito.when(Context.getUserService()).thenReturn(userService);
    Mockito.when(userService.getRole(CFLConstants.SUPER_USER_ROLE_NAME)).thenReturn(superUserRole);
    Mockito.when(userService.getRole(CFLConstants.PRIVILEGE_LEVEL_FULL_ROLE_NAME))
        .thenReturn(fullPrivilegeRole);
    Mockito.when(userService.getPrivilege(Mockito.any(String.class)))
        .thenAnswer(
            invocationOnMock -> {
              Privilege privilege = new Privilege();
              privilege.setPrivilege((String) invocationOnMock.getArguments()[0]);
              return privilege;
            });
    Mockito.when(userService.getUserByUsername(CFLConstants.ADMIN_USER_NAME)).thenReturn(adminUser);

    PowerMockito.when(Context.getAdministrationService()).thenReturn(administrationService);

    PowerMockito.when(Context.getService(CountryPropertyService.class))
        .thenReturn(countryPropertyService);
    Mockito.when(
            countryPropertyService.getCountryPropertyValue(
                Mockito.any(Concept.class), Mockito.any(String.class)))
        .thenReturn(Optional.empty());

    PowerMockito.when(Context.getPersonService()).thenReturn(personService);

    PowerMockito.when(
            Context.getRegisteredComponent("metadataDeployService", MetadataDeployService.class))
        .thenReturn(metadataDeployService);
    PowerMockito.when(Context.getRegisteredComponents(MetadataBundle.class))
        .thenReturn(Collections.singletonList(mockMetadataBundle));
    PowerMockito.when(Context.getService(HtmlFormEntryService.class))
        .thenReturn(htmlFormEntryService);

    PowerMockito.when(
            Context.getRegisteredComponent(
                MessagesConstants.SCHEDULER_SERVICE, MessagesSchedulerService.class))
        .thenReturn(messagesSchedulerService);

    PowerMockito.doAnswer(
            invocationOnMock -> {
              GlobalProperty property =
                  new GlobalProperty(
                      (String) invocationOnMock.getArguments()[0],
                      (String) invocationOnMock.getArguments()[1],
                      (String) invocationOnMock.getArguments()[2]);
              createdGlobalProperties.add(property);
              return property;
            })
        .when(
            GlobalPropertyUtils.class,
            "createGlobalSettingIfNotExists",
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString());

    PowerMockito.doAnswer(
            invocationOnMock -> {
              GlobalProperty property =
                  new GlobalProperty(
                      (String) invocationOnMock.getArguments()[0],
                      (String) invocationOnMock.getArguments()[1],
                      null);
              createdGlobalProperties.add(property);
              return property;
            })
        .when(
            GlobalPropertyUtils.class,
            "createGlobalSettingIfNotExists",
            Mockito.anyString(),
            Mockito.anyString());

    PowerMockito.doAnswer(
            invocationOnMock -> {
              GPDefinition definition = (GPDefinition) invocationOnMock.getArguments()[0];
              GlobalProperty property =
                  new GlobalProperty(
                      definition.getKey(),
                      definition.getDefaultValue(),
                      definition.getDescription());
              createdGlobalProperties.add(property);
              return property;
            })
        .when(
            GlobalPropertyUtils.class,
            "createGlobalSettingIfNotExists",
            Mockito.any(GPDefinition.class));

    PowerMockito.doAnswer(
            invocationOnMock -> {
              CountryProperty property = new CountryProperty();
              property.setName((String) invocationOnMock.getArguments()[0]);
              property.setValue((String) invocationOnMock.getArguments()[1]);
              property.setDescription((String) invocationOnMock.getArguments()[2]);
              createdCountryProperties.add(property);
              return property;
            })
        .when(
            CountryPropertyUtils.class,
            "createDefaultCountrySettingIfNotExists",
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString());
  }

  @Test
  public void shouldAttachProgramsManagingPrivilegesToSuperUser() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    Mockito.verify(userService).saveRole(superUserRole);
    Assert.assertEquals(
        new HashSet<>(CFLConstants.PROGRAM_MANAGING_PRIVILEGES_NAMES),
        superUserRole.getPrivileges().stream()
            .map(Privilege::getPrivilege)
            .collect(Collectors.toSet()));
  }

  @Test
  public void shouldEnsureCorrectRolesAreAssignedToAdmin() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    Assert.assertTrue(adminUser.getRoles().contains(fullPrivilegeRole));
  }

  @Test
  public void shouldCreatePersonOverviewConfig() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    Assert.assertTrue(
        hasPropertyBeenCreated(
            ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY,
            ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE,
            ConfigConstants.FIND_PERSON_FILTER_STRATEGY_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY,
            ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE,
            ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DESCRIPTION));
  }

  @Test
  public void shouldCreateGlobalSettings() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
            CFLConstants.PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE,
            CFLConstants.PATIENT_DASHBOARD_REDIRECT_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_KEY,
            CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE,
            CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.SUPPORTED_ACTOR_TYPE,
            CFLConstants.SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE,
            CFLConstants.SUPPORTED_ACTOR_TYPE_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION,
            CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE,
            CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY,
            CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE,
            CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY,
            CFLConstants.PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE,
            CFLConstants.PERSON_IDENTIFIER_SOURCE_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_KEY,
            CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE,
            CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY,
            CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE,
            CFLConstants.PERSON_LOCATION_ATTRIBUTE_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.CONDITION_LIST_CLASSES_KEY,
            CFLConstants.CONDITION_LIST_CLASSES_DEFAULT_VALUE,
            CFLConstants.CONDITION_LIST_CLASSES_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY,
            CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DEFAULT_VALUE,
            CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.VACCINATION_PROGRAM_KEY,
            CFLConstants.VACCINATION_PROGRAM_DEFAULT_VALUE,
            CFLConstants.VACCINATION_PROGRAM_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY,
            CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DEFAULT_VALUE,
            CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.ACTOR_TYPES_KEY, CFLConstants.CAREGIVER_RELATIONSHIP_UUID, null));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE,
            CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_VALUE,
            CFLConstants.NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY,
            CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DEFAULT_VALUE,
            CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY,
            CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DEFAULT_VALUE,
            CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.VACCINATION_LISTENER_KEY,
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME,
            CFLConstants.VACCINATION_LISTENER_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_KEY,
            CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE,
            CFLConstants.HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            CFLConstants.ENVIRONMENT_KEY,
            CFLConstants.ENVIRONMENT_DEFAULT_VALUE,
            CFLConstants.ENVIRONMENT_DESCRIPTION));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            GlobalPropertiesConstants.VISIT_FORM_URIS.getKey(),
            GlobalPropertiesConstants.VISIT_FORM_URIS.getDefaultValue(),
            GlobalPropertiesConstants.VISIT_FORM_URIS.getDescription()));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
            GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID.getDefaultValue(),
            GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID
                .getDescription()));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
            GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID.getDefaultValue(),
            GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID.getDescription()));
    Assert.assertTrue(
        hasPropertyBeenCreated(
            GlobalPropertiesConstants.SCHEDULED_TASK_CONFIG_CLASS_NAMES.getKey(),
            GlobalPropertiesConstants.SCHEDULED_TASK_CONFIG_CLASS_NAMES.getDefaultValue(),
            GlobalPropertiesConstants.SCHEDULED_TASK_CONFIG_CLASS_NAMES.getDescription()));
  }

  @Test
  public void shouldRegisterEventListeners() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    PowerMockito.verifyStatic();
    CflEventListenerHelper.registerEventListeners();
  }

  @Test
  public void shouldCreatePersonAttributeTypes() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    ArgumentCaptor<PersonAttributeType> newPersonAttributeTypeCapture =
        ArgumentCaptor.forClass(PersonAttributeType.class);
    Mockito.verify(personService).savePersonAttributeType(newPersonAttributeTypeCapture.capture());
    PersonAttributeType newPersonAttributeType = newPersonAttributeTypeCapture.getValue();
    Assert.assertEquals(
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_NAME, newPersonAttributeType.getName());
    Assert.assertEquals(
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_FORMAT, newPersonAttributeType.getFormat());
    Assert.assertEquals(
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_DESCRIPTION,
        newPersonAttributeType.getDescription());
    Assert.assertEquals(
        CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_TYPE_UUID, newPersonAttributeType.getUuid());
    Assert.assertTrue(newPersonAttributeType.getSearchable());
  }

  @Test
  public void shouldSetupStandardMetadata() throws Exception {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    PowerMockito.verifyStatic();
    MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
  }

  @Test
  public void shouldInstallMetadataBundles() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    ArgumentCaptor<Collection> bundleCollectionCapture = ArgumentCaptor.forClass(Collection.class);
    Mockito.verify(metadataDeployService).installBundles(bundleCollectionCapture.capture());
    Assert.assertTrue(bundleCollectionCapture.getValue().contains(mockMetadataBundle));
  }

  @Test
  public void shouldAddTagHandlers() {
    CFLModuleActivator activator = new CFLModuleActivator();
    activator.started();

    Mockito.verify(htmlFormEntryService)
        .addHandler(Mockito.eq(CFLConstants.REGIMEN_TAG_NAME), Mockito.any(TagHandler.class));
  }

  private boolean hasPropertyBeenCreated(String name, String defaultValue, String description) {
    return createdGlobalProperties.stream()
        .filter(property -> Objects.equals(name, property.getProperty()))
        .filter(property -> Objects.equals(defaultValue, property.getValueReference()))
        .anyMatch(property -> Objects.equals(description, property.getDescription()));
  }

  private boolean hasCountryPropertyBeenCreated(
      String name, String defaultValue, String description) {
    return createdCountryProperties.stream()
        .filter(property -> Objects.equals(name, property.getName()))
        .filter(property -> Objects.equals(defaultValue, property.getValue()))
        .anyMatch(property -> Objects.equals(description, property.getDescription()));
  }
}
