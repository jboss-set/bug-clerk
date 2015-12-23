package org.jboss.jbossset.bugclerk.checks.utils;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    private static final int NB_DAYS_BY_WEEK = 7;
    private static final int NB_HOURS_BY_DAY = 24;

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
        return someWeeksAgo(2);
    }

    private static Date someWeeksAgo(final int nbWeek) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -(nbWeek * NB_DAYS_BY_WEEK * NB_HOURS_BY_DAY));
        return cal.getTime();

    }

    public static Date threeWeeksAgo() {
        return someWeeksAgo(3);
    }

}
