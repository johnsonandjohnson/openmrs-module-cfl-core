package org.openmrs.module.cfldistribution.api.metadata.views;

import org.openmrs.module.cfldistribution.api.metadata.services.AbstractMessageServiceMetadata;
import org.openmrs.module.messages.api.model.Template;

import java.io.IOException;

public class DbViewsMetadata extends AbstractMessageServiceMetadata {

  private static final String DROP_VIEW_IF_EXISTS_STATEMENT = "DROP VIEW IF EXISTS ";

  private static final String DIGITS_VIEW_PATH = "mysql/views/digits/";

  private static final String DATES_LIST_TABLE_NAME = "DATES_LIST_10K_DAYS_TABLE";

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
    createDbViews();
  }

  @Override
  protected Template createOrUpdateTemplate() {
    // nothing to do
    return null;
  }

  @Override
  protected void createAndSaveTemplateFields(Template template) {
    // nothing to do
  }

  private void createDbViews() throws IOException {
    createDigitsView();
    createDatesListTable();
  }

  private void createDigitsView() throws IOException {
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "DIGITS_0");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Digits_0.sql"));
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "DIGITS_1");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Digits_1.sql"));
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "DIGITS_2");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Digits_2.sql"));
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "DIGITS_3");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Digits_3.sql"));
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "DIGITS_4");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Digits_4.sql"));
    executeQuery(DROP_VIEW_IF_EXISTS_STATEMENT + "NUMBERS_100");
    executeQuery(getQuery(DIGITS_VIEW_PATH + "Numbers_100.sql"));
  }

  private void createDatesListTable() throws IOException {
    executeQuery("DROP TABLE IF EXISTS " + DATES_LIST_TABLE_NAME + ";");
    executeQuery("CREATE TABLE " + DATES_LIST_TABLE_NAME + " (selected_date date DEFAULT NULL);");
    executeQuery(
        "CREATE INDEX selected_date_idx ON " + DATES_LIST_TABLE_NAME + " (selected_date);");
    executeQuery(getQuery("mysql/views/dates/DatesList_10kDays.sql"));
  }
}
