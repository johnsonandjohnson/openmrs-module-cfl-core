package org.openmrs.module.cfldistribution.api.builder;

import org.openmrs.module.messages.api.model.Template;

public final class MessageTemplateBuilder {

    public static Template buildMessageTemplate(String serviceQuery, String serviceQueryType, String calendarServiceQuery,
                                                String name, boolean shouldUseOptimizedQuery, String uuid) {
        Template template = new Template();
        template.setServiceQuery(serviceQuery);
        template.setServiceQueryType(serviceQueryType);
        template.setCalendarServiceQuery(calendarServiceQuery);
        template.setName(name);
        template.setShouldUseOptimizedQuery(shouldUseOptimizedQuery);
        template.setUuid(uuid);
        return template;
    }
}
