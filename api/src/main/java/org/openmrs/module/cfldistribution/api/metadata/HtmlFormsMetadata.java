package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.Arrays;
import java.util.List;

/** Loads all HTML forms from `omod/src/main/resources/htmlforms`. */
public class HtmlFormsMetadata extends VersionedMetadataBundle {

  @Override
  public int getVersion() {
    return 4;
  }

  @Override
  protected void installEveryTime() {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws Exception {
    final ResourceFactory resourceFactory = ResourceFactory.getInstance();
    final FormService formService = Context.getFormService();
    final HtmlFormEntryService htmlFormEntryService =
        Context.getService(HtmlFormEntryService.class);

    final List<String> htmlforms =
        Arrays.asList(
            "cfldistribution:htmlforms/cfl-HIV.xml",
            "cfldistribution:htmlforms/cfl-check-in.xml",
            "cfldistribution:htmlforms/cfl-visit-note.xml",
            "cfldistribution:htmlforms/cfl-medicine-refill.xml",
            "cfldistribution:htmlforms/cfl-sputum-visit-note.xml",
            "cfldistribution:htmlforms/encounters-form.xml",
            "cfldistribution:htmlforms/cfl-hiv-enrollment.xml",
            "cfldistribution:htmlforms/discontinue-program.xml");

    for (String htmlform : htmlforms) {
      HtmlFormUtil.getHtmlFormFromUiResource(
          resourceFactory, formService, htmlFormEntryService, htmlform);
    }
  }
}
