package org.openmrs.module.cflcore.fragment.controller.manageAccount;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Collections;
import java.util.HashMap;

public class TextFragmentControllerTest extends BaseContextMockTest {
  @Mock private AppFrameworkService appFrameworkService;

  @Mock private PersonService personService;

  @Before
  public void setupMocks() {
    this.contextMockHelper.setService(AppFrameworkService.class, appFrameworkService);
    this.contextMockHelper.setService(PersonService.class, personService);
  }

  @Test
  public void controller_shouldCreateNewPersonAttribute() {
    final PersonAttributeType testType = new PersonAttributeType();
    testType.setUuid("7f91742e-6236-41cf-b927-a8fd47b3f006");

    final Extension testExtension = new Extension();
    testExtension.setExtensionParams(new HashMap<>());
    testExtension.getExtensionParams().put("type", "personAttribute");
    testExtension.getExtensionParams().put("uuid", testType.getUuid());
    testExtension.getExtensionParams().put("formFieldName", "field1");

    Mockito.when(
            appFrameworkService.getExtensionsForCurrentUser(
                "userAccount.personAttributeEditFragment"))
        .thenReturn(Collections.singletonList(testExtension));

    Mockito.when(personService.getPersonAttributeTypeByUuid(testType.getUuid()))
        .thenReturn(testType);

    final FragmentModel fragmentModel = Mockito.mock(FragmentModel.class);
    final User user = Mockito.mock(User.class);
    final FragmentConfiguration fragmentConfiguration = new FragmentConfiguration();
    final TextFragmentController textFragmentController = new TextFragmentController();
    final Person person = new Person();

    textFragmentController.controller(
        fragmentModel, null, fragmentConfiguration, appFrameworkService, user, person);

    Mockito.verify(personService).savePerson(person);
    Assert.assertFalse(person.getAttributes().isEmpty());

    final PersonAttribute newPersonAttribute = person.getAttributes().iterator().next();
    Assert.assertEquals(testType, newPersonAttribute.getAttributeType());
    Assert.assertEquals(".", newPersonAttribute.getValue());
  }
}
