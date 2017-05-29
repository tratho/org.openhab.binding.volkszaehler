package org.openhab.binding.volkszaehler.internal.data;

import java.time.Month;
import java.util.List;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.volkszaehler.internal.Time;

public class Gas {
    private Double currentConsumption;
    private List<Double> listOfGasConsumption;

    public Gas(Double currentConsumption, List<Double> listOfGasConsumption) {
        this.currentConsumption = currentConsumption;
        this.listOfGasConsumption = listOfGasConsumption;
    }

    public State getCurrentConsumption() {
        if (currentConsumption != null) {
            double value = Utility.round(currentConsumption);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State isThermeRunning() {
        if (currentConsumption != null) {
            if (currentConsumption > 0.0) {
                return OnOffType.ON;
            } else {
                return OnOffType.OFF;
            }
        }
        return UnDefType.NULL;
    }

    public State getConsumptionCurrentMonth() {
        return getConsumptionMonth(new Time().getMonth());
    }

    public State getConsumptionMonth(Month month) {
        int index = month.getValue() - 1;
        if (listOfGasConsumption.get(index) != null) {
            double value = Utility.round(listOfGasConsumption.get(index));
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getConsumptionCurrentYear() {
        boolean checker = false;

        double gasConsumptionCurrentYear = 0.0;
        for (int month = 0; month < 12; month++) {
            Double gasConsumptionMonth = listOfGasConsumption.get(month);
            if (gasConsumptionMonth != null) {
                gasConsumptionCurrentYear += listOfGasConsumption.get(month);
                checker = true;
            }
        }
        if (checker) {
            double value = Utility.round(gasConsumptionCurrentYear);
            return new DecimalType(value);
        } else {
            return UnDefType.NULL;
        }
    }
}
