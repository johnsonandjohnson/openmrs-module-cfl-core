/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller.manageAccount;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TextFragmentController {
  private static final String FORM_FIELD_NAME_PARAM = "formFieldName";
  private static final String PERSON_ATTRIBUTE_UUID_PARAM = "personAttributeUuid";

  public void controller(
      FragmentModel model,
      HttpSession session,
      FragmentConfiguration config,
      @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
      @RequestParam(value = "userId", required = false) User user,
      @RequestParam(value = "personId", required = false) Person person) {

    ObjectMapper mapper = new ObjectMapper();
    List<Extension> customPersonAttributeEditFragments = getAllExtensions(appFrameworkService);
    model.addAttribute("customPersonAttributeEditFragments", customPersonAttributeEditFragments);
    SimpleObject so =
        createCustomPersonAttributeJson(config, person, customPersonAttributeEditFragments);
    try {
      model.addAttribute("customPersonAttributeJson", mapper.writeValueAsString(so));
    } catch (IOException e) {
      model.addAttribute("customPersonAttributeJson", "{}");
    }
  }

  @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
  private SimpleObject createCustomPersonAttributeJson(
      FragmentConfiguration config,
      Person person,
      List<Extension> customPersonAttributeEditFragments) {
    SimpleObject so = new SimpleObject();
    for (Extension ext : customPersonAttributeEditFragments) {
      Object type = ext.getExtensionParams().get("type");
      Object personAttributeTypeUuid = ext.getExtensionParams().get("uuid");
      if (type.toString().equals("personAttribute")) {
        String formFieldName = ext.getExtensionParams().get(FORM_FIELD_NAME_PARAM).toString();
        if (person != null && type != null && personAttributeTypeUuid != null) {
          PersonAttribute personAttribute =
              person.getAttribute(
                  Context.getPersonService()
                      .getPersonAttributeTypeByUuid(personAttributeTypeUuid.toString()));
          if (personAttribute == null) {
            PersonAttributeType personAttributeType =
                Context.getPersonService()
                    .getPersonAttributeTypeByUuid(personAttributeTypeUuid.toString());
            personAttribute = new PersonAttribute(personAttributeType, ".");
            person.getAttributes().add(personAttribute);
            Context.getPersonService().savePerson(person);
          }
          String personAttributeUuid = personAttribute.getUuid();
          SimpleObject personAttributeInfo = new SimpleObject();
          personAttributeInfo.put(FORM_FIELD_NAME_PARAM, formFieldName);
          personAttributeInfo.put(PERSON_ATTRIBUTE_UUID_PARAM, personAttributeUuid);
          so.put("personAttributeInfo" + formFieldName, personAttributeInfo);
        }
        if (formFieldName.equals(config.getAttribute(FORM_FIELD_NAME_PARAM))
            && ext.getExtensionParams().containsKey("config")) {
          config.putAll((Map<? extends String, ?>) ext.getExtensionParams().get("config"));
        }
      }
    }
    return so;
  }

  private List<Extension> getAllExtensions(AppFrameworkService appFrameworkService) {
    List<Extension> customPersonAttributeEditFragments =
        appFrameworkService.getExtensionsForCurrentUser("userAccount.personAttributeEditFragment");
    Collections.sort(customPersonAttributeEditFragments);
    return customPersonAttributeEditFragments;
  }
}
