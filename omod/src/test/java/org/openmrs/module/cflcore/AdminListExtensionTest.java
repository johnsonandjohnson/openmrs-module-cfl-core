/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore;

import org.junit.Test;
import org.openmrs.module.Extension;
import org.openmrs.module.cflcore.extension.html.AdminList;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * This test validates the AdminList extension class
 */
public class AdminListExtensionTest {

    /**
     * Get the links for the extension class
     */
    @Test
    public void testValidatesLinks() {
        AdminList ext = new AdminList();

        Map<String, String> links = ext.getLinks();

        assertThat(links, is(notNullValue()));
        assertThat(links.size(), is(not(0)));
    }

    /**
     * Check the media type of this extension class
     */
    @Test
    public void testMediaTypeIsHtml() {
        AdminList ext = new AdminList();

        assertThat(ext.getMediaType(), is(Extension.MEDIA_TYPE.html));
    }
}
