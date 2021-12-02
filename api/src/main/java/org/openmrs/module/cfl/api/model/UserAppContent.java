package org.openmrs.module.cfl.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAppContent {

  private String id;

  private String description;

  private List<UserAppExtension> extensions;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<UserAppExtension> getExtensions() {
    return extensions;
  }

  public void setExtensions(List<UserAppExtension> extensions) {
    this.extensions = extensions;
  }
}
