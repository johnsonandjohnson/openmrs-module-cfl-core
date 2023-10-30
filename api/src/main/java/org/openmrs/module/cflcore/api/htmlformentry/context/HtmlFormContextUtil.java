/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.context;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Obs;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.Regimen;
import org.openmrs.module.cflcore.api.htmlformentry.model.RegimenDrug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class HtmlFormContextUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(HtmlFormContextUtil.class);

  private HtmlFormContextUtil() {}

  public static List<Regimen> getRegimensWithDrugs() {
    return Context.getOrderSetService().getOrderSets(true).stream()
        .map(HtmlFormContextUtil::newRegimen)
        .collect(Collectors.toList());
  }

  public static List<RegimenDrug> createDrugsFromRegimen(OrderSet regimen) {
    List<RegimenDrug> regimenDrugs = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    for (OrderSetMember regimenDrug : regimen.getOrderSetMembers()) {
      try {
        regimenDrugs.add(mapper.readValue(regimenDrug.getOrderTemplate(), RegimenDrug.class));
      } catch (IOException ex) {
        LOGGER.error("Error occurred while converting JSON string value to RegimenDrug class");
      }
    }
    return regimenDrugs;
  }

  public static String getObsStringValue(Obs obs) {
    if (obs == null) {
      return null;
    }

    return obs.getValueAsString(Context.getLocale());
  }

  private static Regimen newRegimen(OrderSet regimenOrderSet) {
    return new Regimen(
        regimenOrderSet.getName(),
        regimenOrderSet.getRetired(),
        createDrugsFromRegimen(regimenOrderSet));
  }
}
