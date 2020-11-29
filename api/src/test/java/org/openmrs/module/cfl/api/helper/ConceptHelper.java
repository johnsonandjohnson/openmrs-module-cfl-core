package org.openmrs.module.cfl.api.helper;

import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public final class ConceptHelper {

    public static Concept buildConcept(String name) {
        Concept concept = new Concept();
        concept.setFullySpecifiedName(buildConceptName(name, "eng"));
        concept.setDescriptions(buildConceptDescriptions());
        return concept;
    }

    private static ConceptName buildConceptName(String name, String language) {
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        conceptName.setLocale(new Locale(language));
        return conceptName;
    }

    private static Collection<ConceptDescription> buildConceptDescriptions() {
        Collection<ConceptDescription> conceptDescriptions = new ArrayList<ConceptDescription>();
        conceptDescriptions.add(buildConceptDescription("eng", "Hello patient"));
        conceptDescriptions.add(buildConceptDescription("nld", "Hallo patiënt"));
        conceptDescriptions.add(buildConceptDescription("spa", "Hola paciente"));
        return conceptDescriptions;
    }

    private static ConceptDescription buildConceptDescription(String language, String description) {
        ConceptDescription conceptDescription = new ConceptDescription();
        conceptDescription.setLocale(new Locale(language));
        conceptDescription.setDescription(description);
        return conceptDescription;
    }

    private ConceptHelper(){
    }
}
