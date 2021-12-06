package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdherenceReportDailyMetadata extends AbstractMessageServiceMetadata {

  @Override
  public int getVersion() {
    return 2;
  }

  @Override
  protected void installEveryTime() {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws IOException {
    createAdherenceReportDailyTemplateResources();
  }

  @Override
  public Template createOrUpdateTemplate() throws IOException {
    String templateUuid = "9556482a-20b2-11ea-ac12-0242c0a82002";
    Template adherenceReportDailyTemplate = getTemplateByUuid(templateUuid);
    String adherenceReportDailyServiceQuery =
        getQuery(SERVICES_BASE_PATH + "AdherenceReportDaily/AdherenceReportDaily.sql");

    if (adherenceReportDailyTemplate == null) {
      adherenceReportDailyTemplate =
          MessageTemplateBuilder.buildMessageTemplate(
              adherenceReportDailyServiceQuery,
              SQL_QUERY_TYPE,
              null,
              "Adherence report daily",
              false,
              templateUuid);
    }

    adherenceReportDailyTemplate.setServiceQuery(adherenceReportDailyServiceQuery);
    return getTemplateService().saveOrUpdate(adherenceReportDailyTemplate);
  }

  @Override
  public void createAndSaveTemplateFields(Template template) {
    List<TemplateField> templateFields = new ArrayList<>();

    if (isTemplateFieldNotExist("95566224-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Service type",
              true,
              "Deactivate service",
              template,
              TemplateFieldType.SERVICE_TYPE,
              "Deactivate service|SMS|Call",
              "95566224-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("95567627-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Week day of delivering message",
              true,
              "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday",
              template,
              TemplateFieldType.DAY_OF_WEEK,
              null,
              "95567627-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("955687fc-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Start of daily messages",
              false,
              "",
              template,
              TemplateFieldType.START_OF_MESSAGES,
              null,
              "955687fc-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9556992c-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "End of daily messages",
              true,
              "NO_DATE|EMPTY",
              template,
              TemplateFieldType.END_OF_MESSAGES,
              null,
              "9556992c-20b2-11ea-ac12-0242c0a82002"));
    }

    templateFields.forEach(getTemplateFieldService()::saveOrUpdate);
  }

  private void createAdherenceReportDailyTemplateResources() throws IOException {
    Template adherenceReportDailyTemplate = createOrUpdateTemplate();
    createAndSaveTemplateFields(adherenceReportDailyTemplate);
    executeQuery("DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_ADHERENCE_DAILY;");
    executeQuery(
        getQuery(
            SERVICES_BASE_PATH + "AdherenceReportDaily/AdherenceReportDailyStartDateFunction.sql"));
  }
}
