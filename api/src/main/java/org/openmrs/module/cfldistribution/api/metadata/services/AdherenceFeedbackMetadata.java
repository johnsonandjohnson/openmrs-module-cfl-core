package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdherenceFeedbackMetadata extends AbstractMessageServiceMetadata {

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
    createAdherenceFeedbackTemplateResources();
  }

  @Override
  protected Template createOrUpdateTemplate() throws IOException {
    String templateUuid = "9556a62d-20b2-11ea-ac12-0242c0a82002";
    Template adherenceFeedbackTemplate = getTemplateByUuid(templateUuid);
    String adherenceFeedbackServiceQuery =
        getQuery(SERVICES_BASE_PATH + "AdherenceFeedback/AdherenceFeedback.sql");

    if (adherenceFeedbackTemplate == null) {
      adherenceFeedbackTemplate =
          MessageTemplateBuilder.buildMessageTemplate(
              adherenceFeedbackServiceQuery,
              SQL_QUERY_TYPE,
              null,
              "Adherence feedback",
              false,
              templateUuid);
    }

    adherenceFeedbackTemplate.setServiceQuery(adherenceFeedbackServiceQuery);
    return getTemplateService().saveOrUpdate(adherenceFeedbackTemplate);
  }

  @Override
  protected void createAndSaveTemplateFields(Template template) {
    List<TemplateField> templateFields = new ArrayList<>();

    if (isTemplateFieldNotExist("9556b39a-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Service type",
              true,
              "Deactivate service",
              template,
              TemplateFieldType.SERVICE_TYPE,
              "Deactivate service|SMS|Call",
              "9556b39a-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9556c330-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Frequency of the message",
              true,
              "Weekly",
              template,
              TemplateFieldType.MESSAGING_FREQUENCY_WEEKLY_OR_MONTHLY,
              null,
              "9556c330-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9556d458-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Week day of delivering message",
              true,
              "Monday",
              template,
              TemplateFieldType.DAY_OF_WEEK,
              null,
              "9556d458-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9556e428-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "Start of messages",
              false,
              "",
              template,
              TemplateFieldType.START_OF_MESSAGES,
              null,
              "9556e428-20b2-11ea-ac12-0242c0a82002"));
    }

    if (isTemplateFieldNotExist("9556f070-20b2-11ea-ac12-0242c0a82002")) {
      templateFields.add(
          MessageTemplateFieldBuilder.buildMessageTemplateField(
              "End of messages",
              true,
              "NO_DATE|EMPTY",
              template,
              TemplateFieldType.END_OF_MESSAGES,
              null,
              "9556f070-20b2-11ea-ac12-0242c0a82002"));
    }

    templateFields.forEach(getTemplateFieldService()::saveOrUpdate);
  }

  private void createAdherenceFeedbackTemplateResources() throws IOException {
    Template adherenceFeedbackTemplate = createOrUpdateTemplate();
    createAndSaveTemplateFields(adherenceFeedbackTemplate);
    executeQuery("DROP FUNCTION IF EXISTS GET_PREDICTION_START_DATE_FOR_ADHERENCE_FEEDBACK;");
    executeQuery(
        getQuery(SERVICES_BASE_PATH + "AdherenceFeedback/AdherenceFeedbackStartDateFunction.sql"));
  }
}
