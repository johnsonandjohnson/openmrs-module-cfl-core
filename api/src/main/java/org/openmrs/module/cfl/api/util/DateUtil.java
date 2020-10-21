package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.openmrs.module.cfl.CFLConstants.DATETIME_WITH_ZONE_FORMAT;

public final class DateUtil {

    public static final TimeZone DEFAULT_SYSTEM_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getDefaultSystemTimeZone());
    }

    public static Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public static TimeZone getDefaultSystemTimeZone() {
        return DEFAULT_SYSTEM_TIME_ZONE;
    }

    public static String convertToDateTimeWithZone(Date date) {
        return convertDate(date, DATETIME_WITH_ZONE_FORMAT);
    }

    private static String convertDate(Date date, String toFormat) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    public static Date addDaysToDate(Date date, int days) {
        return DateUtils.addDays(date, days);
    }

    private DateUtil() {
    }
}
