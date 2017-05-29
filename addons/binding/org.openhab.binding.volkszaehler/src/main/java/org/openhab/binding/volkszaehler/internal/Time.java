/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler.internal;

import java.time.Month;
import java.util.Calendar;

import org.eclipse.smarthome.core.library.types.DateTimeType;

/**
 * The {@link Time}
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class Time {
    private long timeInMilliseconds;

    public Time() {
        this.timeInMilliseconds = Calendar.getInstance().getTimeInMillis();
    }

    public Time(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public Time(String dateAndTime) {
        StringBuilder temp = new StringBuilder();
        temp.append(dateAndTime);
        while (temp.indexOf("\"") >= 0) {
            temp.delete(temp.indexOf("\""), temp.indexOf("\"") + 1);
        }

        String[] temp1 = temp.toString().split(" ");
        String[] dateString = temp1[0].split("-");
        String[] timeString = temp1[1].split(":");

        int year = Integer.parseInt(dateString[0]);
        int month = Integer.parseInt(dateString[1]) - 1;
        int day = Integer.parseInt(dateString[2]);
        int hour = Integer.parseInt(timeString[0]);
        int min = Integer.parseInt(timeString[1]);
        int sec = Integer.parseInt(timeString[2]);

        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, min, sec);
        this.timeInMilliseconds = date.getTimeInMillis();
    }

    public Time(int year, int month, int day, int hour, int min, int sec) {
        month = month - 1;

        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, min, sec);
        this.timeInMilliseconds = date.getTimeInMillis();
    }

    public int getYear() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return date.get(Calendar.YEAR);
    }

    public Month getMonth() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return Month.of(date.get(Calendar.MONTH) + 1);
    }

    public int getDay() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return date.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return date.get(Calendar.MINUTE);
    }

    public int getSecond() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        return date.get(Calendar.SECOND);
    }

    public int getDayOfWeek() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.timeInMilliseconds);
        int day = date.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
    }

    public long getTimeInMillis() {
        return this.timeInMilliseconds;
    }

    public DateTimeType getDateTimeType() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMilliseconds);
        return new DateTimeType(date);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\"");

        buffer.append(getYear());
        buffer.append("-");

        if (getMonth().getValue() < 10) {
            buffer.append("0");
        }
        buffer.append(getMonth().getValue());

        buffer.append("-");

        if (getDay() < 10) {
            buffer.append("0");
        }
        buffer.append(getDay());

        buffer.append(" ");

        if (getHour() < 10) {
            buffer.append("0");
        }
        buffer.append(getHour());

        buffer.append(":");

        if (getMinute() < 10) {
            buffer.append("0");
        }
        buffer.append(getMinute());

        buffer.append(":");

        if (getSecond() < 10) {
            buffer.append("0");
        }
        buffer.append(getSecond());

        buffer.append("\"");

        return buffer.toString();
    }

    public static Time getFirstOfMonth(int month) {
        Time time = new Time(new Time().getYear(), month, 1, 0, 0, 0);
        return time;
    }

    public static Time getLastOfMonth(int month) {
        Time time = null;
        if (month == 2) {
            time = new Time(new Time().getYear(), month, 28, 23, 59, 59);
        }
        if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
                || (month == 12)) {
            time = new Time(new Time().getYear(), month, 31, 23, 59, 59);
        }

        if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
            time = new Time(new Time().getYear(), month, 30, 23, 59, 59);
        }
        return time;
    }
}
