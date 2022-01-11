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

public class HealthTipMetadata extends AbstractMessageServiceMetadata {
  private static final int VERSION = 2;

  public HealthTipMetadata(DbSessionFactory dbSessionFactory) {
    super(dbSessionFactory, VERSION, "9556f9ab-20b2-11ea-ac12-0242c0a82002");
  }

  @Override
  protected Template createTemplate() throws IOException {
    final String healthTipServiceQuery = getHealthTipServiceQuery();

    return MessageTemplateBuilder.buildMessageTemplate(
        healthTipServiceQuery, SQL_QUERY_TYPE, null, "Health tip", false, templateUuid);
  }

  @Override
  protected void updateTemplate(Template template) throws IOException {
    final String healthTipServiceQuery = getHealthTipServiceQuery();
    template.setServiceQuery(healthTipServiceQuery);
  }

  @Override
  protected void createAndSaveTemplateFields(Template template) {
    List<TemplateField> templateFields = new ArrayList<>();

    if (isTemplateFieldNotExist("95570356-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Service type",
              true,
              "Deactivate service",
              template,
              TemplateFieldType.SERVICE_TYPE,
              "Deactivate service|SMS|Call",
              "95570356-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("95570d01-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Frequency of the message",
              true,
              "Daily",
              template,
              TemplateFieldType.MESSAGING_FREQUENCY_DAILY_OR_WEEKLY_OR_MONTHLY,
              null,
              "95570d01-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("955716a6-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Week day of delivering message",
              true,
              "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday",
              template,
              TemplateFieldType.DAY_OF_WEEK,
              null,
              "955716a6-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9557232f-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Start of messages",
              false,
              "",
              template,
              TemplateFieldType.START_OF_MESSAGES,
              null,
              "9557232f-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("95572cd9-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Categories of the message",
              true,
              "HT_PREVENTION",
              template,
              TemplateFieldType.CATEGORY_OF_MESSAGE,
              null,
              "95572cd9-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("95573791-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "End of messages",
              true,
              "NO_DATE|EMPTY",
              template,
              TemplateFieldType.END_OF_MESSAGES,
              null,
              "95573791-20b2-11ea-ac12-0242c0a82002"));
    }

    templateFields.forEach(getTemplateFieldService()::saveOrUpdate);
  }

  @Override
  protected void performAdditionalUpdate() throws IOException {
    metadataSQLScriptRunner.executeQuery(
        "DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_HEALTH_TIP;");
    metadataSQLScriptRunner.executeQueryFromResource(
        SERVICES_BASE_PATH + "HealthTip/HealthTipStartDateFunction.sql");
  }

  private String getHealthTipServiceQuery() throws IOException {
    return metadataSQLScriptRunner.getQueryFromResource(
        SERVICES_BASE_PATH + "HealthTip/HealthTip.sql");
  }
}
