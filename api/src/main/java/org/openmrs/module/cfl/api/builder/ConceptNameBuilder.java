package org.openmrs.module.cfl.api.builder;

import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;

import java.util.Locale;

public class ConceptNameBuilder {

  private String name;

  private ConceptNameType conceptNameType;

  private Locale locale;

  public ConceptNameBuilder() {
    conceptNameType = ConceptNameType.FULLY_SPECIFIED;
    locale = Locale.ENGLISH;
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
