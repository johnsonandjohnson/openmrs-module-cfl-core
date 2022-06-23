/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;

public class ConceptBuilder {

  private ConceptDatatype conceptDatatype;

  private ConceptClass conceptClass;

  private boolean isSet;

  private ConceptName fullySpecifiedName;

  private ConceptName shortName;

  public Concept build() {
    Concept concept = new Concept();
    concept.setDatatype(conceptDatatype);
    concept.setConceptClass(conceptClass);
    concept.setSet(isSet);
    concept.setFullySpecifiedName(fullySpecifiedName);
    concept.setShortName(shortName);
    return concept;
  }

  public ConceptBuilder withConceptDatatype(ConceptDatatype conceptDatatype) {
    this.conceptDatatype = conceptDatatype;
    return this;
  }

  public ConceptBuilder withConceptClass(ConceptClass conceptClass) {
    this.conceptClass = conceptClass;
    return this;
  }

  public ConceptBuilder withIsSet(boolean isSet) {
    this.isSet = isSet;
    return this;
  }

  public ConceptBuilder withFullySpecifiedName(ConceptName fullySpecifiedName) {
    this.fullySpecifiedName = fullySpecifiedName;
    return this;
  }

  public ConceptBuilder withShortName(ConceptName shortName) {
    this.shortName = shortName;
    return this;
  }
}
