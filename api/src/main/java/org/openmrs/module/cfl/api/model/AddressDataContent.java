/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddressDataContent that = (AddressDataContent) o;
    return Objects.equals(results, that.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(results);
  }
}
