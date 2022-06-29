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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OrderSet;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.htmlformentry.widget.DropdownWidget;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.module.htmlformentry.widget.Option;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RegimenElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

  private DropdownWidget valueWidget;

  private ErrorWidget errorValueWidget;

  private Concept concept;

  private Obs existingObs;

  public RegimenElement(FormEntryContext context, Map<String, String> parameters) {
    initializeFields(context, parameters);
    prepareWidget(context);
  }

  @Override
  public Collection<FormSubmissionError> validateSubmission(
      FormEntryContext formEntryContext, HttpServletRequest httpServletRequest) {
    return Collections.emptySet();
  }

  @Override
  public void handleSubmission(
      FormEntrySession formEntrySession, HttpServletRequest httpServletRequest) {
    String regimenValue =
        (String) valueWidget.getValue(formEntrySession.getContext(), httpServletRequest);
    if (StringUtils.isNotBlank(regimenValue)) {
      createOrUpdateObs(formEntrySession, regimenValue);
    }
  }

  @Override
  public String generateHtml(FormEntryContext formEntryContext) {
    StringBuilder sb = new StringBuilder();
    if (valueWidget != null) {
      sb.append(valueWidget.generateHtml(formEntryContext));
    }
    if (errorValueWidget != null) {
      sb.append(errorValueWidget.generateHtml(formEntryContext));
    }
    return sb.toString();
  }

  private void createOrUpdateObs(FormEntrySession formEntrySession, String regimenValue) {
    FormEntryContext.Mode mode = formEntrySession.getContext().getMode();
    if (mode == FormEntryContext.Mode.ENTER) {
      formEntrySession
          .getSubmissionActions()
          .createObs(concept, regimenValue, DateUtil.now(), null);
    } else if (mode == FormEntryContext.Mode.EDIT) {
      createOrUpdateObsInEditMode(formEntrySession, regimenValue);
    }
  }

  private void createOrUpdateObsInEditMode(FormEntrySession formEntrySession, String regimenValue) {
    FormSubmissionActions actions = formEntrySession.getSubmissionActions();
    Date date = DateUtil.now();

    if (existingObs == null) {
      actions.createObs(concept, regimenValue, date, null);
    } else if (isRegimenValueChanged(existingObs, regimenValue)) {
      actions.modifyObs(existingObs, concept, regimenValue, date, null);
    }
  }

  private boolean isRegimenValueChanged(Obs existingObs, String regimenValue) {
    return !StringUtils.equalsIgnoreCase(existingObs.getValueText(), regimenValue);
  }

  private void initializeFields(FormEntryContext context, Map<String, String> parameters) {
    setRegimenConcept(parameters);

    if (CollectionUtils.isNotEmpty(context.getCurrentObsGroupConcepts())) {
      existingObs = context.getObsFromCurrentGroup(concept, (Concept) null);
    }
  }

  private void setRegimenConcept(Map<String, String> parameters) {
    String conceptIdParamValue = parameters.get("conceptId");
    if (conceptIdParamValue != null) {
      concept = HtmlFormEntryUtil.getConcept(conceptIdParamValue);
      if (concept == null) {
        throw new IllegalArgumentException(
            "Cannot find concept for value "
                + conceptIdParamValue
                + " in conceptId attribute value. Parameters: "
                + parameters);
      }
    }
  }

  private void prepareWidget(FormEntryContext context) {
    valueWidget = new DropdownWidget();
    configureRegimenDropdown();
    errorValueWidget = new ErrorWidget();
    context.registerWidget(valueWidget);
    context.registerErrorWidget(valueWidget, errorValueWidget);
  }

  private void configureRegimenDropdown() {
    List<OrderSet> orderSets = Context.getOrderSetService().getOrderSets(false);
    if (CollectionUtils.isEmpty(orderSets)) {
      valueWidget.addOption(new Option("", "", false));
    } else {
      for (OrderSet orderSet : orderSets) {
        String regimenName = orderSet.getName();
        valueWidget.addOption(new Option(regimenName, regimenName, false));
      }
    }
    setInitialValue();
  }

  private void setInitialValue() {
    String initialValue = "";
    if (existingObs != null) {
      initialValue = existingObs.getValueText();
    }
    valueWidget.setInitialValue(initialValue);
  }
}
