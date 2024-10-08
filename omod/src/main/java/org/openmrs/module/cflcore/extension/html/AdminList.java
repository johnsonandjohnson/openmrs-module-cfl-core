/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the "cfl.title"
 * heading. This extension is enabled by defining (uncommenting) it in the config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
     */
    public String getTitle() {
        return "cfl.title";
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
     */
    public Map<String, String> getLinks() {
        final Map<String, String> map = new HashMap<>();
        map.put("module/cfl/adHocMessage.form", "cfl.adHocMessage.title");
        map.put("module/cfl/countryList.form", "cfl.manageCountries.title");
        map.put("/ms/uiframework/resource/cfl/swagger/index.html", "cfl.restApi.title");
        return map;
    }
}
