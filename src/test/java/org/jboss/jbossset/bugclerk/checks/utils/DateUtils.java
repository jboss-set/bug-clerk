package org.jboss.jbossset.bugclerk.checks.utils;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    private static final int NB_DAYS_BY_WEEK = 7;
    private static final int NB_HOURS_BY_DAY = 24;

    private static final int LAST_WEEK = 1;
    private static final int TWO_WEEKS_AGO = 2;
    private static final int THREE_WEEKS_AGO = 3;

    private DateUtils() {
    }

    public static Date twoMonthAgo() {
        return someMonthAgo(2);
    }

    public static Date threeMonthAgo() {
        return someMonthAgo(3);
    }

    private static Date someMonthAgo(final int nbMonth) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -nbMonth);
        return cal.getTime();
    }

    public static Date twoWeeksAgo() {
        return someWeeksAgo(TWO_WEEKS_AGO);
    }

    private static Date someWeeksAgo(final int nbWeek) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -(nbWeek * NB_DAYS_BY_WEEK * NB_HOURS_BY_DAY));
        return cal.getTime();

    }

    public static Date threeWeeksAgo() {
        return someWeeksAgo(THREE_WEEKS_AGO);
    }

    public static Date lastWeek() {
        return someWeeksAgo(LAST_WEEK);
    }

}
