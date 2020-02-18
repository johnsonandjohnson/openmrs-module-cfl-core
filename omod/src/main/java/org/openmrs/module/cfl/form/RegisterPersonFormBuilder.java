/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.cfl.form;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.NavigableFormStructure;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.Section;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.CONFIG;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.FRAGMENT_ID;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_ATTRIBUTE_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PROVIDER_NAME;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.SECTIONS;

/**
 * Builds a registration form structure from the app configuration.
 * Based on openmrs-module-registrationapp v1.13.0
 * omod/src/main/java/org/openmrs/module/registrationapp/form/RegisterPatientFormBuilder.java
 */
public final class RegisterPersonFormBuilder {

    private static final Log LOGGER = LogFactory.getLog(RegisterPersonFormBuilder.class);
    private static final String MULTI_VALUES_WARN = "Multiple values for a single person attribute type not supported," +
            " ignoring extra values";

    /**
     * Builds the navigable form structure for the specified app descriptor
     *
     * @param app the app descriptor
     * @return the form structure
     */
    public static NavigableFormStructure buildFormStructure(AppDescriptor app) {
        NavigableFormStructure formStructure = new NavigableFormStructure();

        // Get the ordered list of sections out of the configuration
        Map<String, Section> configuredSections = new LinkedHashMap<String, Section>();
        ArrayNode sections = (ArrayNode) app.getConfig().get(SECTIONS);
        for (JsonNode i : sections) {
            ObjectNode config = (ObjectNode) i;

            ObjectMapper objectMapper = new ObjectMapper();
            Section section = objectMapper.convertValue(config, Section.class);

            if (section.getQuestions() != null) {
                for (Question question : section.getQuestions()) {
                    if (question.getFields() != null) {
                        for (Field field : question.getFields()) {
                            ObjectNode widget = field.getWidget();
                            String providerName = widget.get(PROVIDER_NAME).getTextValue();
                            String fragmentId = widget.get(FRAGMENT_ID).getTextValue();
                            JsonNode fieldConfig = widget.get(CONFIG);
                            //Groovy doesn't know how to handle ArrayNode and ObjectNode therefore we need to convert
                            //them to List and Map respectively. Also TextNode.toString() includes quotes, we need
                            //to extract the actual text value excluding the quotes
                            FragmentConfiguration fragConfig = new FragmentConfiguration((Map) flatten(fieldConfig));
                            FragmentRequest fragmentRequest = new FragmentRequest(providerName, fragmentId, fragConfig);
                            field.setFragmentRequest(fragmentRequest);
                        }
                    }
                }
            }

            configuredSections.put(section.getId(), section);
        }

        for (Section section : configuredSections.values()) {
            formStructure.addSection(section);
        }
        return formStructure;
    }

    /**
     * A utility method that converts the specified JsonNode to a value that we can be used in
     * groovy. If it's a TextNode it extracts the actual text and if it's an ArrayNode or ObjectNode
     * it gets converted to a List or Map respectively. Note that the method returns the same value
     * for other node types and recursively applies the same logic to nested arrays and objects.
     *
     * @param node a JsonNode to flatten
     * @return the flattened value
     */
    private static Object flatten(JsonNode node) {
        Object obj = node;
        if (node != null) {
            if (node.isTextual()) {
                obj = node.getTextValue();
            } else if (node.isBoolean()) {
                obj = node.getBooleanValue();
            } else if (node.isNumber()) {
                obj = node.getNumberValue();
            } else if (node.isArray()) {
                List<Object> list = new ArrayList<Object>();
                Iterator<JsonNode> itemIterator = node.getElements();
                while (itemIterator.hasNext()) {
                    list.add(flatten(itemIterator.next()));
                }
                obj = list;
            } else if (node.isObject()) {
                Map<String, Object> map = new HashMap<String, Object>();
                Iterator<String> fieldNameIterator = node.getFieldNames();
                while (fieldNameIterator.hasNext()) {
                    String fName = fieldNameIterator.next();
                    map.put(fName, flatten(node.get(fName)));
                }
                obj = map;
            }
        }
        return obj;
    }

    public static void resolvePersonAttributeFields(NavigableFormStructure form, Person person,
                                                    Map<String, String[]> parameterMap) {
        for (Field field : form.getFields()) {
            if (StringUtils.equals(field.getType(), PERSON_ATTRIBUTE_PROP)) {
                String[] parameterValues = parameterMap.get(field.getFormFieldName());
                if (parameterValues != null && parameterValues.length > 0) {
                    if (parameterValues.length > 1) {
                        LOGGER.warn(MULTI_VALUES_WARN);
                    }
                    String parameterValue = parameterValues[0];
                    if (parameterValue != null) {
                        PersonAttributeType attributeType = Context.getPersonService()
                                .getPersonAttributeTypeByUuid(field.getUuid());
                        if (attributeType != null) {
                            PersonAttribute attribute = new PersonAttribute(attributeType, parameterValue);
                            person.addAttribute(attribute);
                        }
                    }
                }
            }
        }
    }

    private RegisterPersonFormBuilder() {
    }
}
