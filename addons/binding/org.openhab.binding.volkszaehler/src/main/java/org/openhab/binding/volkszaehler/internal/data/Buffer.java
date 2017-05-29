package org.openhab.binding.volkszaehler.internal.data;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

public class Buffer {
    private Double temperatureBottom;
    private Double temperatureMiddle;
    private Double temperatureTop;

    public Buffer(Double temperatureBottom, Double temperatureMiddle, Double temperatureTop) {
        this.temperatureBottom = temperatureBottom;
        this.temperatureMiddle = temperatureMiddle;
        this.temperatureTop = temperatureTop;
    }

    public State getTemperatureTop() {
        if (temperatureTop != null) {
            double value = Utility.round(temperatureTop);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getTemperatureMiddle() {
        if (temperatureMiddle != null) {
            double value = Utility.round(temperatureMiddle);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getTemperatureBottom() {
        if (temperatureBottom != null) {
            double value = Utility.round(temperatureBottom);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getTemperatureAverage() {
        int counter = 0;
        Double sumOfTemp = null;
        if (temperatureTop != null) {
            sumOfTemp = temperatureTop;
            counter++;
        }
        if (temperatureMiddle != null) {
            if (sumOfTemp == null) {
                sumOfTemp = temperatureMiddle;
            } else {
                sumOfTemp += temperatureMiddle;
            }
            counter++;
        }
        if (temperatureBottom != null) {
            if (sumOfTemp == null) {
                sumOfTemp = temperatureBottom;
            } else {
                sumOfTemp += temperatureBottom;
            }
            counter++;
        }
        if (sumOfTemp != null) {
            double value = sumOfTemp / counter;
            value = Utility.round(value);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }
}
