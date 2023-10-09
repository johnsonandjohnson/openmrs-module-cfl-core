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
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.service.TreatmentService;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;

public class TreatmentFunctionsContextContentProvider implements VelocityContextContentProvider {
  @Override
  public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
    formEntrySession.addToVelocityContext(
        "treatmentFunctions",
        new TreatmentFunctions(formEntrySession, Context.getService(TreatmentService.class)));
  }
}
