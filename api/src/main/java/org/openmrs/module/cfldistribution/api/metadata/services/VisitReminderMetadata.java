package org.openmrs.module.cfldistribution.api.metadata.services;

import org.openmrs.module.cfldistribution.api.builder.MessageTemplateBuilder;
import org.openmrs.module.cfldistribution.api.builder.MessageTemplateFieldBuilder;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class VisitReminderMetadata extends AbstractMessageServiceMetadata {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() {
        // nothing to do
    }

    @Override
    protected void installNewVersion() throws IOException {
        createVisitReminderTemplateResources();
    }

    @Override
    protected Template createAndSaveTemplate() throws IOException {
        String visitReminderServiceQuery = getQuery(SERVICES_BASE_PATH + "VisitReminder/VisitReminder.sql");
        String visitReminderCalendarServiceQuery = getQuery(SERVICES_BASE_PATH +
                "VisitReminder/VisitReminderCalendarQuery.sql");
        Template visitReminderTemplate = MessageTemplateBuilder.buildMessageTemplate(
                visitReminderServiceQuery, SQL_QUERY_TYPE, visitReminderCalendarServiceQuery,
                "Visit reminder", true, "95573fe3-20b2-11ea-ac12-0242c0a82002");
        return getTemplateService().saveOrUpdate(visitReminderTemplate);
    }

    @Override
    protected void createAndSaveTemplateFields(Template template) {
        List<TemplateField> templateFields = Arrays.asList(
                MessageTemplateFieldBuilder.buildMessageTemplateField("Service type", true,
                        "Deactivate service", template, TemplateFieldType.SERVICE_TYPE,
                        "Deactivate service|SMS|Call", "95574976-20b2-11ea-ac12-0242c0a82002"),

                MessageTemplateFieldBuilder.buildMessageTemplateField("Start of messages",
                        false, "", template, TemplateFieldType.START_OF_MESSAGES,
                        null, "95575327-20b2-11ea-ac12-0242c0a82002"),

                MessageTemplateFieldBuilder.buildMessageTemplateField("End of messages", true,
                        "NO_DATE|EMPTY", template, TemplateFieldType.END_OF_MESSAGES, null,
                        "95575cbd-20b2-11ea-ac12-0242c0a82002"));

        templateFields.forEach(getTemplateFieldService()::saveOrUpdate);
    }

    private void createVisitReminderTemplateResources() throws IOException {
        Template visitReminderTemplate = createAndSaveTemplate();
        createAndSaveTemplateFields(visitReminderTemplate);
        executeQuery(getQuery(SERVICES_BASE_PATH + "VisitReminder/VisitReminderStartDateFunction.sql"));
    }
}
