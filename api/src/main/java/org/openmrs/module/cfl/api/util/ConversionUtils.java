package org.openmrs.module.cfl.api.util;

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
