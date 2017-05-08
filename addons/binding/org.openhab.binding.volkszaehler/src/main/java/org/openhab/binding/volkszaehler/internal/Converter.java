/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler.internal;

/**
 * The {@link Converter}
 *
 * @author Thomas Traunbauer - Initial contribution
 */
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
