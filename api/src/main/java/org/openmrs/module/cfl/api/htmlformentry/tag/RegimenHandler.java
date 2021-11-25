package org.openmrs.module.cfl.api.htmlformentry.tag;

import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.htmlformentry.handler.TagHandler;

import java.util.Map;

public class RegimenHandler extends SubstitutionTagHandler implements TagHandler {

  @Override
  protected String getSubstitution(
      FormEntrySession formEntrySession,
      FormSubmissionController formSubmissionController,
      Map<String, String> parameters) {
    RegimenElement element = new RegimenElement(formEntrySession.getContext());
    formEntrySession.getSubmissionController().addAction(element);
    return element.generateHtml(formEntrySession.getContext());
  }
}
