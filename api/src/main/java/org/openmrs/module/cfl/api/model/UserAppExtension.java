package org.openmrs.module.cfl.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAppExtension {

  private String id;

  private String extensionPointId;

  private String type;

  private Integer order;

  private String label;

  private String icon;

  private String script;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getExtensionPointId() {
    return extensionPointId;
  }

  public void setExtensionPointId(String extensionPointId) {
    this.extensionPointId = extensionPointId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }
}
