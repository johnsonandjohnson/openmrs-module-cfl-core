/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.tag;

import org.openmrs.Concept;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.htmlformentry.handler.TagHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RegimenHandler extends SubstitutionTagHandler implements TagHandler {

  @Override
  protected List<AttributeDescriptor> createAttributeDescriptors() {
    List<AttributeDescriptor> attributeDescriptors = new ArrayList<>();
    attributeDescriptors.add(new AttributeDescriptor("conceptId", Concept.class));
    return Collections.unmodifiableList(attributeDescriptors);
  }

  @Override
  protected String getSubstitution(
      FormEntrySession formEntrySession,
      FormSubmissionController formSubmissionController,
      Map<String, String> parameters) {
    FormEntryContext context = formEntrySession.getContext();
    RegimenElement element = new RegimenElement(context, parameters);
    formEntrySession.getSubmissionController().addAction(element);
    context.pushToStack(element);
    return element.generateHtml(context);
  }
}
