package org.openmrs.module.cfl.api.util;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DateUtilTest {
    @Test
    public void getDateWithTimeOfDay_shouldReturnCorrectDate() throws ParseException {
        // given
        final TimeZone testTimeZone = TimeZone.getTimeZone("UTC");
        final Calendar calendar = Calendar.getInstance(testTimeZone);
        calendar.set(2021, Calendar.MAY, 6, 13, 23, 45);
        calendar.set(Calendar.MILLISECOND, 765);
        final Date date = calendar.getTime();
        final String timeStr = "14:11";

        // when
        final Date result = DateUtil.getDateWithTimeOfDay(date, timeStr, testTimeZone);

        // then
        final Calendar expectedCalendar = Calendar.getInstance(testTimeZone);
        expectedCalendar.set(2021, Calendar.MAY, 6, 14, 11, 0);
        expectedCalendar.set(Calendar.MILLISECOND, 0);
        final Date expectedDate = expectedCalendar.getTime();

        assertNotNull(result);
        assertThat(result, is(expectedDate));
    }
}
