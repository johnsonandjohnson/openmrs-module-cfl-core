/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

import org.apache.commons.lang.StringUtils;

public final class ConversionUtils {
    private ConversionUtils() {
        // static only
    }

    public static Number toNumber(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return (Number) value;
        }

        final String valueAsString = value.toString();

        if (StringUtils.isBlank(valueAsString)) {
            return null;
        }

        return Integer.parseInt(valueAsString);
    }
}
