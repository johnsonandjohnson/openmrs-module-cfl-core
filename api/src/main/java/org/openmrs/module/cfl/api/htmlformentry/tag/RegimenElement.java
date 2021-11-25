package org.openmrs.module.cfl.api.htmlformentry.tag;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.OrderSet;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.htmlformentry.widget.DropdownWidget;
import org.openmrs.module.htmlformentry.widget.Option;
import org.openmrs.module.htmlformentry.widget.Widget;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegimenElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

  protected Widget valueWidget;

  public RegimenElement(FormEntryContext context) {
    valueWidget = new DropdownWidget();
    List<OrderSet> orderSets = Context.getOrderSetService().getOrderSets(false);
    if (CollectionUtils.isEmpty(orderSets)) {
      ((DropdownWidget) valueWidget).addOption(new Option("", "", false));
    } else {
      for (OrderSet orderSet : orderSets) {
        String regimenName = orderSet.getName();
        ((DropdownWidget) valueWidget).addOption(new Option(regimenName, regimenName, false));
      }
    }
    valueWidget.setInitialValue("");
    context.registerWidget(valueWidget);
  }

  @Override
  public Collection<FormSubmissionError> validateSubmission(
      FormEntryContext formEntryContext, HttpServletRequest httpServletRequest) {
    return Collections.emptySet();
  }

  @Override
  public void handleSubmission(
      FormEntrySession formEntrySession, HttpServletRequest httpServletRequest) {}

  @Override
  public String generateHtml(FormEntryContext formEntryContext) {
    StringBuilder sb = new StringBuilder();
    if (valueWidget != null) {
      sb.append(valueWidget.generateHtml(formEntryContext));
    }
    return sb.toString();
  }
}
