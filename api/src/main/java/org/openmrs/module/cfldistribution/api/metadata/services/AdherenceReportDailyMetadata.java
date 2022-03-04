package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdherenceReportDailyMetadata extends AbstractMessageServiceMetadata {
  private static final int VERSION = 2;

  public AdherenceReportDailyMetadata(DbSessionFactory dbSessionFactory) {
    super(dbSessionFactory, VERSION, "9556482a-20b2-11ea-ac12-0242c0a82002");
  }

  @Override
  protected Template createTemplate() throws IOException {
    final String adherenceReportDailyServiceQuery = getAdherenceReportDailyServiceQuery();
    final String adherenceReportDailyCalendarQuery = getAdherenceReportDailyCalendarQuery();

    return MessageTemplateBuilder.buildMessageTemplate(
        adherenceReportDailyServiceQuery,
        SQL_QUERY_TYPE,
        adherenceReportDailyCalendarQuery,
        "Adherence report daily",
        true,
        templateUuid);
  }

  @Override
  protected void updateTemplate(Template template) throws IOException {
    final String adherenceReportDailyServiceQuery = getAdherenceReportDailyServiceQuery();
    final String adherenceReportDailyCalendarQuery = getAdherenceReportDailyCalendarQuery();

    template.setServiceQuery(adherenceReportDailyServiceQuery);
    template.setCalendarServiceQuery(adherenceReportDailyCalendarQuery);
    template.setShouldUseOptimizedQuery(true);
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

  @Override
  protected void performAdditionalUpdate() throws IOException {
    metadataSQLScriptRunner.executeQuery(
        "DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_ADHERENCE_DAILY;");
    metadataSQLScriptRunner.executeQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportDaily/AdherenceReportDailyStartDateFunction.sql");
  }

  private String getAdherenceReportDailyServiceQuery() throws IOException {
    return metadataSQLScriptRunner.getQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportDaily/AdherenceReportDaily.sql");
  }

  private String getAdherenceReportDailyCalendarQuery() throws IOException {
    return metadataSQLScriptRunner.getQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportDaily/AdherenceReportDailyCalendar.sql");
  }
}
