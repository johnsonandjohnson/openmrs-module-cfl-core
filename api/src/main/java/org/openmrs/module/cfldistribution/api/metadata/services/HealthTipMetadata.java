package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HealthTipMetadata extends AbstractMessageServiceMetadata {

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
    createHealthTipTemplateResources();
  }

  @Override
  protected Template createOrUpdateTemplate() throws IOException {
    String templateUuid = "9556f9ab-20b2-11ea-ac12-0242c0a82002";
    Template healthTipTemplate = getTemplateByUuid(templateUuid);
    String healthTipServiceQuery = getQuery(SERVICES_BASE_PATH + "HealthTip/HealthTip.sql");

    if (healthTipTemplate == null) {
      healthTipTemplate =
          MessageTemplateBuilder.buildMessageTemplate(
              healthTipServiceQuery, SQL_QUERY_TYPE, null, "Health tip", false, templateUuid);
    }

    healthTipTemplate.setServiceQuery(healthTipServiceQuery);
    return getTemplateService().saveOrUpdate(healthTipTemplate);
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

  private void createHealthTipTemplateResources() throws IOException {
    Template healthTipTemplate = createOrUpdateTemplate();
    createAndSaveTemplateFields(healthTipTemplate);
    executeQuery("DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_HEALTH_TIP;");
    executeQuery(getQuery(SERVICES_BASE_PATH + "HealthTip/HealthTipStartDateFunction.sql"));
  }
}
