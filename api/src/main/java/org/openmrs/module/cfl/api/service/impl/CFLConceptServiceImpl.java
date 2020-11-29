package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.api.ConceptService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.CFLConceptService;

public class CFLConceptServiceImpl extends BaseOpenmrsService implements CFLConceptService {

    private ConceptService conceptService;

    public void setConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @Override
    public String getMessageByConceptNameAndLanguage(String conceptName, String language) {
        String message = null;
        Concept concept = conceptService.getConceptByName(conceptName);
        if (concept != null) {
            for (ConceptDescription conceptDescription : concept.getDescriptions()) {
                if (StringUtils.equalsIgnoreCase(conceptDescription.getLocale().getISO3Language(), language) ||
                        StringUtils.equalsIgnoreCase(conceptDescription.getLocale().getLanguage(), language)) {
                    message = conceptDescription.getDescription();
                }
            }
            if (StringUtils.isBlank(message)) {
                message = concept.getDescription().getDescription();
            }
        }
        return message;
    }
}
