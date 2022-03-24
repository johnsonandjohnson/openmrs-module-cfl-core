package org.openmrs.module.cfl.api.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AddressDataContent class - represents results data used in {@link
 * org.openmrs.module.cfl.api.dto.AddressDataDTO}
 */
public class AddressDataContent {

  private List<String> results;

  public AddressDataContent(Object[] objects) {
    List<String> list = new ArrayList<>();
    Arrays.stream(objects).forEach(o -> list.add((String) o));
    this.results = list;
  }

  public List<String> getContent() {
    return results;
  }

  public void setContent(List<String> results) {
    this.results = results;
  }
}
