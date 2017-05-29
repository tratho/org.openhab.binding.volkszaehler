package org.openhab.binding.volkszaehler.internal.data;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

public class Power {
    private Double consumptionGroundfloor;
    private Double consumptionAttic;
    private Double consumptionTotal;
    private Double photovoltaic;
    private Double buy;
    private Double sell;

    public Power(Double consumptionGroundfloor, Double consumptionAttic, Double photovoltaic) {
        this.consumptionGroundfloor = consumptionGroundfloor;
        this.consumptionAttic = consumptionAttic;
        this.photovoltaic = photovoltaic;
        this.sell = 0.0;
        this.buy = 0.0;

        if ((consumptionGroundfloor != null) && (consumptionAttic != null)) {
            this.consumptionTotal = consumptionGroundfloor + consumptionAttic;
        }

        if ((consumptionTotal != null) && (photovoltaic != null)) {
            if (photovoltaic > consumptionTotal) {
                this.sell = photovoltaic - consumptionTotal;
                this.buy = 0.0;
            }
            if (photovoltaic < consumptionTotal) {
                this.sell = 0.0;
                this.buy = consumptionTotal - photovoltaic;
            }
        }
    }

    public State getConsumption() {
        if (consumptionTotal != null) {
            double value = Utility.round(consumptionTotal);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getConsumptionGroundfloor() {
        if (consumptionGroundfloor != null) {
            double value = Utility.round(consumptionGroundfloor);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getConsumptionAttic() {
        if (consumptionAttic != null) {
            double value = Utility.round(consumptionAttic);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getPhotovoltaic() {
        if (photovoltaic != null) {
            double value = Utility.round(photovoltaic);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getBuy() {
        if (buy != null) {
            double value = Utility.round(buy);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }

    public State getSell() {
        if (sell != null) {
            double value = Utility.round(sell);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }
}
