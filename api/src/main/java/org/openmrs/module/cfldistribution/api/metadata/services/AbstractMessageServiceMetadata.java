package org.openmrs.module.cfldistribution.api.metadata.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.service.TemplateFieldService;
import org.openmrs.module.messages.api.service.TemplateService;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class AbstractMessageServiceMetadata extends VersionedMetadataBundle {

  protected static final String SERVICES_BASE_PATH = "mysql/services/";

  protected static final String SQL_QUERY_TYPE = "SQL";

  private DbSessionFactory dbSessionFactory;

  protected abstract Template createOrUpdateTemplate() throws IOException;

  protected abstract void createAndSaveTemplateFields(Template template);

  protected void executeQuery(String query) {
    if (StringUtils.isNotBlank(query)) {
      SQLQuery sqlQuery = dbSessionFactory.getCurrentSession().createSQLQuery(query);
      sqlQuery.executeUpdate();
    }
  }

  protected String getQuery(String resourcesPath) throws IOException {
    String query = null;
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcesPath);
    if (in != null) {
      query = IOUtils.toString(in);
    }
    return query;
  }

  protected boolean isTemplateFieldNotExist(String fieldUuid) {
    return getTemplateFieldService().getByUuid(fieldUuid) == null;
  }

  protected Template getTemplateByUuid(String templateUuid) {
    Optional<Template> template =
        getTemplateService().getAll(true).stream()
            .filter(t -> StringUtils.equalsIgnoreCase(t.getUuid(), templateUuid))
            .findFirst();
    return template.get();
  }

  protected TemplateService getTemplateService() {
    return Context.getRegisteredComponent("messages.templateService", TemplateService.class);
  }

  protected TemplateFieldService getTemplateFieldService() {
    return Context.getRegisteredComponent(
        "messages.templateFieldService", TemplateFieldService.class);
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }
}
