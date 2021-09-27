package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.api.context.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.openmrs.module.cfl.CFLConstants.DATETIME_WITH_ZONE_FORMAT;
import static org.openmrs.module.messages.api.constants.ConfigConstants.DEFAULT_USER_TIMEZONE;

public final class DateUtil {

    public static final TimeZone DEFAULT_SYSTEM_TIME_ZONE = TimeZone.getTimeZone("UTC");

    /**
     * The date-time pattern which contains only hour of day and minute of hour fields.
     */
    public static final String HOUR_AND_MINUTE_PATTERN = "HH:mm";

    public static final String MIDNIGHT_TIME = "00:00";

    public static final String DATE_AND_TIME_AND_TIME_ZONE_PATTERN = "yyyyMMddHHmmz";

    private DateUtil() {
    }

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date nowInSystemTimeZone() {
        return getDateWithTimeZone(new Date(), getSystemTimeZone());
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getDefaultSystemTimeZone());
    }

    public static Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    //Returns server time zone
    public static TimeZone getSystemTimeZone() {
        return TimeZone.getTimeZone(ZoneId.systemDefault());
    }

    //Returns always UTC time zone
    public static TimeZone getDefaultSystemTimeZone() {
        return DEFAULT_SYSTEM_TIME_ZONE;
    }

    public static TimeZone getDefaultUserTimezone() {
        final String defaultTimezoneName = Context.getAdministrationService().getGlobalProperty(DEFAULT_USER_TIMEZONE);
        return TimeZone.getTimeZone(defaultTimezoneName);
    }

    public static String convertToDateTimeWithZone(Date date) {
        return convertDate(date, DATETIME_WITH_ZONE_FORMAT);
    }

    public static Date parseStringToDate(String date, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.parse(date);
    }

    public static String convertDate(Date date, String toFormat) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    public static Date addDaysToDate(Date date, int days) {
        return DateUtils.addDays(date, days);
    }

    public static Date getTomorrow(final TimeZone timeZone) {
        final Calendar calendar = Calendar.getInstance(timeZone);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * Gets a Date object representing an instant of time where the date-part is equal to the date-part of {@code date}
     * and time-part is equal to time parsed from {@code timeOfDay}, the seconds and milliseconds are both 0.
     * <p>
     * The {@code timeOfDay} has to fit {@link #HOUR_AND_MINUTE_PATTERN} pattern.
     * </p>
     * <p>
     * Example: <br />
     * For a {@code date} of `2001-07-04T12:08:56.235` and {@code timeOfDay} equal to `14:11`, the result of this method
     * will be equal to `2001-07-04T14:11:00.000`.
     * </p>
     *
     * @param date      the source of date-part, not null
     * @param timeOfDay the source of time-part, not null
     * @param timeZone  the time zone of the {@code date} and {@code timeOfDay}, not null
     * @return the Date object with date-time part from {{@code date} and time-part from {@code timeOfDay}, never null
     * @throws IllegalArgumentException if {@code timeOfDay} has illegal value
     */
    public static Date getDateWithTimeOfDay(Date date, String timeOfDay, TimeZone timeZone) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat(HOUR_AND_MINUTE_PATTERN);
        timeFormat.setTimeZone(timeZone);

        try {
            final Calendar timePart = DateUtils.toCalendar(timeFormat.parse(timeOfDay));
            timePart.setTimeZone(timeZone);

            final Calendar result = DateUtils.toCalendar(date);
            result.setTimeZone(timeZone);
            result.set(Calendar.HOUR_OF_DAY, timePart.get(Calendar.HOUR_OF_DAY));
            result.set(Calendar.MINUTE, timePart.get(Calendar.MINUTE));
            result.set(Calendar.SECOND, 0);
            result.set(Calendar.MILLISECOND, 0);
            return result.getTime();
        } catch (ParseException pe) {
            throw new IllegalArgumentException(
                    "Illegal value of timeOfDay=" + timeOfDay + "; expected pattern: " + HOUR_AND_MINUTE_PATTERN, pe);
        }
    }
}
