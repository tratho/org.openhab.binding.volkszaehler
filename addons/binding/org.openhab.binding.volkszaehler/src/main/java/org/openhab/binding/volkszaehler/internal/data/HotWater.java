package org.openhab.binding.volkszaehler.internal.data;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

public class HotWater {
    private Double hotWaterTemperature;

    public HotWater(Double hotWaterTemperature) {
        this.hotWaterTemperature = hotWaterTemperature;
    }

    public State getTemperature() {
        if (hotWaterTemperature != null) {
            double value = Utility.round(hotWaterTemperature);
            return new DecimalType(value);
        }
        return UnDefType.NULL;
    }
}
