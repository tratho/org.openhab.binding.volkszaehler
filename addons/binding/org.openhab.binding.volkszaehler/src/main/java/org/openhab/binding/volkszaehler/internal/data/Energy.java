package org.openhab.binding.volkszaehler.internal.data;

import java.time.Month;
import java.util.List;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.volkszaehler.internal.Time;

public class Energy {
    private List<Double> listOfEnergyMonth;

    public Energy(List<Double> listOfEnergyMonth) {
        this.listOfEnergyMonth = listOfEnergyMonth;
    }

    public State getEnergyMonth(Month month) {
        int index = month.getValue() - 1;
        if (listOfEnergyMonth.get(index) != null) {
            double value = Utility.round(listOfEnergyMonth.get(index));
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getEnergyCurrentMonth() {
        return getEnergyMonth(new Time().getMonth());
    }

    public State getEnergyCurrentYear() {
        boolean checker = false;
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            Double currentMonth = listOfEnergyMonth.get(i);
            if (currentMonth != null) {
                energy += currentMonth;
                checker = true;
            }
        }
        if (checker) {
            double value = Utility.round(energy);
            return new DecimalType(value);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getEnergyPercentageShareYear(State other) {
        if (getEnergyCurrentYear() instanceof DecimalType) {
            if (other instanceof DecimalType) {
                double energyYearTotal = ((DecimalType) getEnergyCurrentYear()).doubleValue()
                        + ((DecimalType) other).doubleValue();
                if (energyYearTotal > 0) {
                    double value = Utility
                            .round((((DecimalType) getEnergyCurrentYear()).doubleValue() / energyYearTotal) * 100);
                    return new DecimalType(value);
                }
            }
        }
        return UnDefType.NULL;
    }

}
