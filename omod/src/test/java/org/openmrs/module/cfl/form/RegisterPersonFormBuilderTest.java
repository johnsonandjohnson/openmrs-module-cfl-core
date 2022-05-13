/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.form;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.NavigableFormStructure;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.Section;
import org.openmrs.ui.framework.fragment.FragmentRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.CONFIG;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.FRAGMENT_ID;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.JSON_NODES_LIMIT;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PROVIDER_NAME;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.SECTIONS;

public class RegisterPersonFormBuilderTest {
    private static final String SECTION_ID = "testSection";
    private static final String WIDGET_PROVIDER_NAME = "testProvider";
    private static final String WIDGET_FRAGMENT_ID = "testFragmentId";

    @Test
    public void flattenMethodShouldHandleJsonArrayUnderLimit() {
        // given
        final ArrayNode test0SizeArray = prepareArrayOfNumbers(0);
        final ArrayNode testMiddleSizeArray = prepareArrayOfNumbers(JSON_NODES_LIMIT / 2);
        final ArrayNode testMaxSizeArray = prepareArrayOfNumbers(JSON_NODES_LIMIT);

        final ObjectNode flattenTestData = JsonNodeFactory.instance.objectNode();
        flattenTestData.put("test0SizeArray", test0SizeArray);
        flattenTestData.put("testMiddleSizeArray", testMiddleSizeArray);
        flattenTestData.put("testMaxSizeArray", testMaxSizeArray);

        final AppDescriptor testAppDescriptor = prepareAppDescriptorForTest(flattenTestData);

        // when
        final NavigableFormStructure testResultNavigableFormStructure =
                RegisterPersonFormBuilder.buildFormStructure(testAppDescriptor);

        // then
        final FragmentRequest resultFragmentRequest = getFragmentRequest(testResultNavigableFormStructure);
        assertResultArray(resultFragmentRequest.getConfiguration().get("test0SizeArray"), 0, 0, 0);
        assertResultArray(resultFragmentRequest.getConfiguration().get("testMiddleSizeArray"), JSON_NODES_LIMIT / 2, 0,
                (JSON_NODES_LIMIT / 2) - 1);
        assertResultArray(resultFragmentRequest.getConfiguration().get("testMaxSizeArray"), JSON_NODES_LIMIT, 0,
                JSON_NODES_LIMIT - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void flattenMethodShouldThrowExceptionForJsonArrayOverLimit() {
        // given
        final ArrayNode testOverLimitSizeArray = prepareArrayOfNumbers(JSON_NODES_LIMIT + 100);

        final ObjectNode flattenTestData = JsonNodeFactory.instance.objectNode();
        flattenTestData.put("testOverLimitSizeArray", testOverLimitSizeArray);

        final AppDescriptor testAppDescriptor = prepareAppDescriptorForTest(flattenTestData);

        // when
        RegisterPersonFormBuilder.buildFormStructure(testAppDescriptor);

        // then
        // expect exception
    }

    private ArrayNode prepareArrayOfNumbers(int size) {
        final ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        for (int i = 0; i < size; ++i) {
            arrayNode.add(i);
        }

        return arrayNode;
    }

    private void assertResultArray(final Object array, int size, int first, int last) {
        assertNotNull(array);

        final List<?> arrayList = (List<?>) array;
        assertEquals(size, arrayList.size());

        if (size > 0) {
            assertEquals(first, arrayList.get(0));
            assertEquals(last, arrayList.get(arrayList.size() - 1));
        }
    }

    @Test
    public void flattenMethodShouldHandleJsonObjectUnderLimit() {
        // given
        final String prefix = "flattenMethodShouldHandleJsonObjectWithLimitApplied";
        final ObjectNode test0SizeObject = prepareObjectOfNumbers(prefix, 0);
        final ObjectNode testMiddleSizeObject = prepareObjectOfNumbers(prefix, JSON_NODES_LIMIT / 2);
        final ObjectNode testMaxSizeObject = prepareObjectOfNumbers(prefix, JSON_NODES_LIMIT);

        final ObjectNode flattenTestData = JsonNodeFactory.instance.objectNode();
        flattenTestData.put("test0SizeObject", test0SizeObject);
        flattenTestData.put("testMiddleSizeObject", testMiddleSizeObject);
        flattenTestData.put("testMaxSizeObject", testMaxSizeObject);

        final AppDescriptor testAppDescriptor = prepareAppDescriptorForTest(flattenTestData);

        // when
        final NavigableFormStructure testResultNavigableFormStructure =
                RegisterPersonFormBuilder.buildFormStructure(testAppDescriptor);

        // then
        final FragmentRequest resultFragmentRequest = getFragmentRequest(testResultNavigableFormStructure);
        assertResultObject(resultFragmentRequest.getConfiguration().get("test0SizeObject"), 0, 0, 0);
        assertResultObject(resultFragmentRequest.getConfiguration().get("testMiddleSizeObject"), JSON_NODES_LIMIT / 2, 0,
                (JSON_NODES_LIMIT / 2) - 1);
        assertResultObject(resultFragmentRequest.getConfiguration().get("testMaxSizeObject"), JSON_NODES_LIMIT, 0,
                JSON_NODES_LIMIT - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void flattenMethodShouldThrowExceptionForJsonObjectOverLimit() {
        // given
        final String prefix = "flattenMethodShouldHandleJsonObjectWithLimitApplied";
        final ObjectNode testOverLimitSizeObject = prepareObjectOfNumbers(prefix, JSON_NODES_LIMIT + 100);

        final ObjectNode flattenTestData = JsonNodeFactory.instance.objectNode();
        flattenTestData.put("testOverLimitSizeObject", testOverLimitSizeObject);

        final AppDescriptor testAppDescriptor = prepareAppDescriptorForTest(flattenTestData);

        // when
        RegisterPersonFormBuilder.buildFormStructure(testAppDescriptor);

        // then
        // expect exception
    }

    private ObjectNode prepareObjectOfNumbers(final String prefix, int size) {
        final ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        for (int i = 0; i < size; ++i) {
            objectNode.put(prefix + i, i);
        }

        return objectNode;
    }

    private void assertResultObject(final Object object, int size, int min, int max) {
        assertNotNull(object);

        final Map<String, ?> objectMap = (Map<String, ?>) object;
        assertEquals(size, objectMap.size());

        if (size > 0) {
            final List<Integer> allValues = new LinkedList<>((Collection<? extends Integer>) objectMap.values());
            Collections.sort(allValues);

            assertEquals(min, allValues.get(0).intValue());
            assertEquals(max, allValues.get(allValues.size() - 1).intValue());
        }
    }

    private FragmentRequest getFragmentRequest(final NavigableFormStructure testResultNavigableFormStructure) {
        assertEquals(1, testResultNavigableFormStructure.getSections().size());

        final Section resultSection = testResultNavigableFormStructure.getSections().get(SECTION_ID);
        assertEquals(1, resultSection.getQuestions().size());

        final Question resultQuestion = resultSection.getQuestions().get(0);
        assertEquals(1, resultQuestion.getFields().size());

        final Field resultField = resultQuestion.getFields().get(0);

        return resultField.getFragmentRequest();
    }

    private AppDescriptor prepareAppDescriptorForTest(final ObjectNode flattenTestData) {
        final ObjectNode widgetNode = JsonNodeFactory.instance.objectNode();
        widgetNode.put(CONFIG, flattenTestData);
        widgetNode.put(PROVIDER_NAME, WIDGET_PROVIDER_NAME);
        widgetNode.put(FRAGMENT_ID, WIDGET_FRAGMENT_ID);

        final ObjectNode fieldNode = JsonNodeFactory.instance.objectNode();
        fieldNode.put("widget", widgetNode);

        final ArrayNode fieldsNode = JsonNodeFactory.instance.arrayNode();
        fieldsNode.add(fieldNode);

        final ObjectNode questionNode = JsonNodeFactory.instance.objectNode();
        questionNode.put("fields", fieldsNode);

        final ArrayNode questionsNode = JsonNodeFactory.instance.arrayNode();
        questionsNode.add(questionNode);

        final ObjectNode sectionNode = JsonNodeFactory.instance.objectNode();
        sectionNode.put("id", SECTION_ID);
        sectionNode.put("questions", questionsNode);

        final ArrayNode sectionsNode = JsonNodeFactory.instance.arrayNode();
        sectionsNode.add(sectionNode);

        final ObjectNode configNode = JsonNodeFactory.instance.objectNode();
        configNode.put(SECTIONS, sectionsNode);

        final AppDescriptor testAppDescriptor = new AppDescriptor();
        testAppDescriptor.setConfig(configNode);

        return testAppDescriptor;
    }
}
