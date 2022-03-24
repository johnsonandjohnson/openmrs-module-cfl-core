package org.openmrs.module.cfl.api.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AddressDataContent class - represents results data used in {@link
 * org.openmrs.module.cfl.api.dto.AddressDataDTO}
 */
public class AddressDataContent {

  private List<String> results;

  public AddressDataContent(Object[] objects) {
    this.results = Arrays.stream(objects).map(o -> (String) o).collect(Collectors.toList());
  }

  public List<String> getContent() {
    return results;
  }

  public void setContent(List<String> results) {
    this.results = results;
  }
}
