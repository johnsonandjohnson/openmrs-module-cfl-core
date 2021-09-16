package org.openmrs.module.cfldistribution.api.metadata.views;

import org.openmrs.module.cfldistribution.api.metadata.services.AbstractMessageServiceMetadata;
import org.openmrs.module.messages.api.model.Template;

import java.io.IOException;

public class DbViewsMetadata extends AbstractMessageServiceMetadata {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() throws Exception {
        // nothing to do
    }

    @Override
    protected void installNewVersion() throws Exception {
        createDbViews();
    }

    @Override
    protected Template createAndSaveTemplate() {
        // nothing to do
        return null;
    }

    @Override
    protected void createAndSaveTemplateFields(Template template) {
        // nothing to do
    }

    private void createDbViews() throws IOException {
        executeQuery(getQuery("mysql/views/digits/Digits_0.sql"));
        executeQuery(getQuery("mysql/views/digits/Digits_1.sql"));
        executeQuery(getQuery("mysql/views/digits/Digits_2.sql"));
        executeQuery(getQuery("mysql/views/digits/Digits_3.sql"));
        executeQuery(getQuery("mysql/views/digits/Digits_4.sql"));
        executeQuery(getQuery("mysql/views/dates/DatesList.sql"));
        executeQuery(getQuery("mysql/views/dates/DatesList_10Days.sql"));
        executeQuery(getQuery("mysql/views/digits/Numbers_100.sql"));
    }
}
