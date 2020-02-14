package org.openmrs.module.cfl.api.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.openmrs.module.cfl.CFLConstants.DATETIME_WITH_ZONE_FORMAT;

public final class DateUtil {

    public static String convertToDateTimeWithZone(Date date) {
        return convertDate(date, DATETIME_WITH_ZONE_FORMAT);
    }

    private static String convertDate(Date date, String toFormat) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    private DateUtil() {
    }
}
