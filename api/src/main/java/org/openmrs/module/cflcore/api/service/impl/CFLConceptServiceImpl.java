/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.api.ConceptService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cflcore.api.service.CFLConceptService;

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
