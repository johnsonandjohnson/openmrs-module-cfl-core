package org.openmrs.module.cfl.api.program;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramConfig {

  private String name;

  private String programFormUuid;

  private String discontinuationFormUuid;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProgramFormUuid() {
    return programFormUuid;
  }

  public void setFormUuid(String programFormUuid) {
    this.programFormUuid = programFormUuid;
  }

  public String getDiscontinuationFormUuid() {
    return discontinuationFormUuid;
  }

  public void setDiscontinuationFormUuid(String discontinuationFormUuid) {
    this.discontinuationFormUuid = discontinuationFormUuid;
  }
}
