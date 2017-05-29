package org.openhab.binding.volkszaehler.internal.data;

public class Utility {
    public static double round(double value) {
        return round(value, 2);
    }

    public static double round(double value, int numberOfFractionDigits) {
        if (numberOfFractionDigits < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, numberOfFractionDigits);
        value *= factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
