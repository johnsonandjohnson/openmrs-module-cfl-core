/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.builder;

import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.Locale;

public class ConceptNameBuilder {

  private String name;

  private ConceptNameType conceptNameType;

  private Locale locale;

  public ConceptNameBuilder() {
    conceptNameType = ConceptNameType.FULLY_SPECIFIED;
    locale = Context.getLocale();
  }

  public ConceptName build() {
    ConceptName conceptName = new ConceptName();
    conceptName.setName(name);
    conceptName.setConceptNameType(conceptNameType);
    conceptName.setLocale(locale);
    return conceptName;
  }

  public ConceptNameBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ConceptNameBuilder withConceptNameType(ConceptNameType conceptNameType) {
    this.conceptNameType = conceptNameType;
    return this;
  }

  public ConceptNameBuilder withLocale(Locale locale) {
    this.locale = locale;
    return this;
  }
}
