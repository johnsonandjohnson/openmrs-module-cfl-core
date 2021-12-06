package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdherenceReportWeeklyMetadata extends AbstractMessageServiceMetadata {

  @Override
  public int getVersion() {
    return 2;
  }

  @Override
  protected void installEveryTime() throws Exception {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws Exception {
    createAdherenceReportWeeklyTemplateResources();
  }

  @Override
  protected Template createOrUpdateTemplate() throws IOException {
    String templateUuid = "96d93c15-3884-11ea-b1e9-0242ac160002";
    Template adherenceReportWeeklyTemplate = getTemplateByUuid(templateUuid);
    String adherenceReportWeeklyServiceQuery =
        getQuery(SERVICES_BASE_PATH + "AdherenceReportWeekly/AdherenceReportWeekly.sql");

    if (adherenceReportWeeklyTemplate == null) {
      adherenceReportWeeklyTemplate =
          MessageTemplateBuilder.buildMessageTemplate(
              adherenceReportWeeklyServiceQuery,
              SQL_QUERY_TYPE,
              null,
              "Adherence report weekly",
              false,
              templateUuid);
    }

    adherenceReportWeeklyTemplate.setServiceQuery(adherenceReportWeeklyServiceQuery);
    return getTemplateService().saveOrUpdate(adherenceReportWeeklyTemplate);
  }

  @Override
  protected void createAndSaveTemplateFields(Template template) {
    List<TemplateField> templateFields = new ArrayList<>();

    if (isTemplateFieldNotExist("ce1ae5a2-3884-11ea-b1e9-0242ac160002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Service type",
              true,
              "Deactivate service",
              template,
              TemplateFieldType.SERVICE_TYPE,
              "Deactivate service|SMS|Call",
              "ce1ae5a2-3884-11ea-b1e9-0242ac160002"));
    }

    if (isTemplateFieldNotExist("dce23c53-3884-11ea-b1e9-0242ac160002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Week day of delivering message",
              true,
              "Monday",
              template,
              TemplateFieldType.DAY_OF_WEEK_SINGLE,
              null,
              "dce23c53-3884-11ea-b1e9-0242ac160002"));
    }

    if (isTemplateFieldNotExist("fc9cf90e-3884-11ea-b1e9-0242ac160002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Start of weekly messages",
              false,
              "",
              template,
              TemplateFieldType.START_OF_MESSAGES,
              null,
              "fc9cf90e-3884-11ea-b1e9-0242ac160002"));
    }

    if (isTemplateFieldNotExist("f0f4750c-3884-11ea-b1e9-0242ac160002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "End of weekly messages",
              true,
              "NO_DATE|EMPTY",
              template,
              TemplateFieldType.END_OF_MESSAGES,
              null,
              "f0f4750c-3884-11ea-b1e9-0242ac160002"));
    }

    templateFields.forEach(getTemplateFieldService()::saveOrUpdate);
  }

  private void createAdherenceReportWeeklyTemplateResources() throws IOException {
    Template adherenceReportWeeklyTemplate = createOrUpdateTemplate();
    createAndSaveTemplateFields(adherenceReportWeeklyTemplate);
    executeQuery("DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_ADHERENCE_WEEKLY;");
    executeQuery(
        getQuery(
            SERVICES_BASE_PATH
                + "AdherenceReportWeekly/AdherenceReportWeeklyStartDateFunction.sql"));
  }
}
