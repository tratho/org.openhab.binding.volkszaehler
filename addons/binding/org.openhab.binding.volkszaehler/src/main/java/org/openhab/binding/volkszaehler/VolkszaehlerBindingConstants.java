/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link VolkszaehlerBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class VolkszaehlerBindingConstants {

    public static final String BINDING_ID = "volkszaehler";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "device");

    public static final String DEVICE_PARAMETER_HOST = "DEVICE_HOST";
    public static final String DEVICE_PARAMETER_DB_NAME = "DEVICE_DB_NAME";
    public static final String DEVICE_PARAMETER_USERNAME = "DEVICE_USER_NAME";
    public static final String DEVICE_PARAMETER_PASSWORD = "DEVICE_PASSWORD";
    public static final String DEVICE_PARAMETER_REFRESH = "REFRESH_INTERVAL";

    // List of all Channel ids
    public static final String CHANNEL_BUFFER_TEMPERATURE_AVERAGE = "bufferTemperatureAverage";
    public static final String CHANNEL_BUFFER_TEMPERATURE_BOTTOM = "bufferTemperatureBottom";
    public static final String CHANNEL_BUFFER_TEMPERATURE_MIDDLE = "bufferTemperatureMiddle";
    public static final String CHANNEL_BUFFER_TEMPERATURE_TOP = "bufferTemperatureTop";

    public static final String CHANNEL_HOT_WATER_TEMPERATURE = "hotWaterTemperature";

    public static final String CHANNEL_GAS_CONSUMPTION = "gasConsumption";
    public static final String CHANNEL_GASTOTHERM_RUNNING = "gastothermRunning";

    public static final String CHANNEL_GAS_CONSUMPTION_CURRENT_MONTH = "gasConsumptionCurrentMonth";
    public static final String CHANNEL_GAS_CONSUMPTION_CURRENT_YEAR = "gasConsumptionCurrentYear";

    public static final String CHANNEL_CURRENT_POWER_ATTIC = "currentPowerAttic";
    public static final String CHANNEL_CURRENT_POWER_GROUNDFLOOR = "currentPowerGroundfloor";
    public static final String CHANNEL_CURRENT_POWER_TOTAL = "currentPowerTotal";
    public static final String CHANNEL_CURRENT_POWER_PHOTOVOLTAIC = "currentPowerPhotovoltaic";
    public static final String CHANNEL_CURRENT_POWER_BUY = "currentPowerBuy";
    public static final String CHANNEL_CURRENT_POWER_SELL = "currentPowerSell";

    public static final String CHANNEL_CURRENT_MONTH_ENERGY_ATTIC = "energyCurrentMonthAttic";
    public static final String CHANNEL_CURRENT_MONTH_ENERGY_GROUNDFLOOR = "energyCurrentMonthGroundfloor";

    public static final String CHANNEL_CURRENT_YEAR_ENERGY_ATTIC = "energyCurrentYearAttic";
    public static final String CHANNEL_CURRENT_YEAR_ENERGY_GROUNDFLOOR = "energyCurrentYearGroundfloor";

    public static final String CHANNEL_PERCENTAGE_SHARE_YEAR_ENERGY_ATTIC = "energyPercentageShareYearAttic";
    public static final String CHANNEL_PERCENTAGE_SHARE_YEAR_ENERGY_GROUNDFLOOR = "energyPercentageShareYearGroundfloor";

    public static final String CHANNEL_MONTH1_ENERGY_ATTIC = "energyJanuaryAttic";
    public static final String CHANNEL_MONTH2_ENERGY_ATTIC = "energyFebruaryAttic";
    public static final String CHANNEL_MONTH3_ENERGY_ATTIC = "energyMarchAttic";
    public static final String CHANNEL_MONTH4_ENERGY_ATTIC = "energyAprilAttic";
    public static final String CHANNEL_MONTH5_ENERGY_ATTIC = "energyMayAttic";
    public static final String CHANNEL_MONTH6_ENERGY_ATTIC = "energyJuneAttic";
    public static final String CHANNEL_MONTH7_ENERGY_ATTIC = "energyJulyAttic";
    public static final String CHANNEL_MONTH8_ENERGY_ATTIC = "energyAugustAttic";
    public static final String CHANNEL_MONTH9_ENERGY_ATTIC = "energySeptemberAttic";
    public static final String CHANNEL_MONTH10_ENERGY_ATTIC = "energyOctoberAttic";
    public static final String CHANNEL_MONTH11_ENERGY_ATTIC = "energyNovemberAttic";
    public static final String CHANNEL_MONTH12_ENERGY_ATTIC = "energyDecemberAttic";

    public static final String CHANNEL_MONTH1_ENERGY_GROUNDFLOOR = "energyJanuaryGroundfloor";
    public static final String CHANNEL_MONTH2_ENERGY_GROUNDFLOOR = "energyFebruaryGroundfloor";
    public static final String CHANNEL_MONTH3_ENERGY_GROUNDFLOOR = "energyMarchGroundfloor";
    public static final String CHANNEL_MONTH4_ENERGY_GROUNDFLOOR = "energyAprilGroundfloor";
    public static final String CHANNEL_MONTH5_ENERGY_GROUNDFLOOR = "energyMayGroundfloor";
    public static final String CHANNEL_MONTH6_ENERGY_GROUNDFLOOR = "energyJuneGroundfloor";
    public static final String CHANNEL_MONTH7_ENERGY_GROUNDFLOOR = "energyJulyGroundfloor";
    public static final String CHANNEL_MONTH8_ENERGY_GROUNDFLOOR = "energyAugustGroundfloor";
    public static final String CHANNEL_MONTH9_ENERGY_GROUNDFLOOR = "energySeptemberGroundfloor";
    public static final String CHANNEL_MONTH10_ENERGY_GROUNDFLOOR = "energyOctoberGroundfloor";
    public static final String CHANNEL_MONTH11_ENERGY_GROUNDFLOOR = "energyNovemberGroundfloor";
    public static final String CHANNEL_MONTH12_ENERGY_GROUNDFLOOR = "energyDecemberGroundfloor";

}
