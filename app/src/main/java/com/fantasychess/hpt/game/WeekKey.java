package com.fantasychess.hpt.game;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/** ISO year-week key, e.g. "2026-W26", used to issue one free pack per week. */
public final class WeekKey {

    private WeekKey() { }

    public static String current() {
        return forMillis(System.currentTimeMillis());
    }

    public static String forMillis(long millis) {
        Calendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(4); // ISO-8601
        cal.setTimeInMillis(millis);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        // In late December a week can belong to next ISO year; Calendar already accounts via WEEK_OF_YEAR.
        return String.format(Locale.US, "%04d-W%02d", year, week);
    }

    /** Monday (ISO start) of the current week as yyyy-MM-dd, for "games this week" filtering. */
    public static String startOfCurrentWeekIso() {
        Calendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }
}
