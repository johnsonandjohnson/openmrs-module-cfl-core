package org.openmrs.module.cfldistribution.api.builder;

import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.TemplateField;
import org.openmrs.module.messages.api.model.TemplateFieldType;

public final class MessageTemplateFieldBuilder {

    public static TemplateField buildMessageTemplateField(String name, boolean isMandatory, String defaultValue,
                                                          Template template, TemplateFieldType fieldType,
                                                          String possibleValues, String uuid) {
        TemplateField templateField = new TemplateField();
        templateField.setName(name);
        templateField.setMandatory(isMandatory);
        templateField.setDefaultValue(defaultValue);
        templateField.setTemplate(template);
        templateField.setTemplateFieldType(fieldType);
        templateField.setPossibleValues(possibleValues);
        templateField.setUuid(uuid);
        return templateField;
    }
}
