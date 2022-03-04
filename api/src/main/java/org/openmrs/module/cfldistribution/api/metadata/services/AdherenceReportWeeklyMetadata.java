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

public class AdherenceReportWeeklyMetadata extends AbstractMessageServiceMetadata {
  private static final int VERSION = 2;

  public AdherenceReportWeeklyMetadata(DbSessionFactory dbSessionFactory) {
    super(dbSessionFactory, VERSION, "96d93c15-3884-11ea-b1e9-0242ac160002");
  }

  @Override
  protected Template createTemplate() throws IOException {
    final String adherenceReportWeeklyServiceQuery = getAdherenceReportWeeklyServiceQuery();
    final String adherenceReportWeeklyCalendarQuery = getAdherenceReportWeeklyCalendarQuery();

    return MessageTemplateBuilder.buildMessageTemplate(
        adherenceReportWeeklyServiceQuery,
        SQL_QUERY_TYPE,
        adherenceReportWeeklyCalendarQuery,
        "Adherence report weekly",
        true,
        templateUuid);
  }

  @Override
  protected void updateTemplate(Template template) throws IOException {
    final String adherenceReportWeeklyServiceQuery = getAdherenceReportWeeklyServiceQuery();
    final String adherenceReportWeeklyCalendarQuery = getAdherenceReportWeeklyCalendarQuery();

    template.setServiceQuery(adherenceReportWeeklyServiceQuery);
    template.setCalendarServiceQuery(adherenceReportWeeklyCalendarQuery);
    template.setShouldUseOptimizedQuery(true);
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

  @Override
  protected void performAdditionalUpdate() throws IOException {
    metadataSQLScriptRunner.executeQuery(
        "DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_ADHERENCE_WEEKLY;");
    metadataSQLScriptRunner.executeQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportWeekly/AdherenceReportWeeklyStartDateFunction.sql");
  }

  private String getAdherenceReportWeeklyServiceQuery() throws IOException {
    return metadataSQLScriptRunner.getQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportWeekly/AdherenceReportWeekly.sql");
  }

  private String getAdherenceReportWeeklyCalendarQuery() throws IOException {
    return metadataSQLScriptRunner.getQueryFromResource(
        SERVICES_BASE_PATH + "AdherenceReportWeekly/AdherenceReportWeeklyCalendar.sql");
  }
}
