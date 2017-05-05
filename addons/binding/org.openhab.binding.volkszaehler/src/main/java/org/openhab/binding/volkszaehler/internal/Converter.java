package org.openhab.binding.volkszaehler.internal;

public class Converter {

    public static Time seconds_to_Time(long value) {
        return new Time(value * 1000);
    }

    public static long min_to_Milliseconds(int value) {
        return value * 60 * 1000;
    }

    public static long hour_to_Milliseconds(int value) {
        return value * 60 * 60 * 1000;
    }

    public static void main(String[] args) {
        System.out.println(seconds_to_Time(1481037311));
        System.out.println(seconds_to_Time(1474495200));
    }

}
