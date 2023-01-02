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

import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.Regimen;
import org.openmrs.module.cflcore.api.htmlformentry.model.RegimenDrug;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegimenContextContentProvider implements VelocityContextContentProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegimenContextContentProvider.class);

  private static final String REGIMENS_PROPERTY_NAME = "regimens";

  @Override
  public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
    try {
      formEntrySession.addToVelocityContext(
          REGIMENS_PROPERTY_NAME, new ObjectMapper().writeValueAsString(getRegimensWithDrugs()));
    } catch (IOException e) {
      LOGGER.error("Unable to write regimens object into JSON string");
    }
  }

  private List<Regimen> getRegimensWithDrugs() {
    List<Regimen> regimens = new ArrayList<>();
    Context.getOrderSetService()
        .getOrderSets(false)
        .forEach(
            regimen ->
                regimens.add(new Regimen(regimen.getName(), createDrugsFromRegimen(regimen))));
    return regimens;
  }

  private List<RegimenDrug> createDrugsFromRegimen(OrderSet regimen) {
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
}
