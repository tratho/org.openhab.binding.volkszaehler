/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
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
    public static final ThingTypeUID THING_TYPE_DEVICE = new ThingTypeUID(BINDING_ID, "device");

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
    public static final String CHANNEL_GAS_THERME_RUNNING = "gastothermRunning";

    public static final String CHANNEL_GAS_CONSUMPTION_CURRENT_MONTH = "gasConsumptionCurrentMonth";
    public static final String CHANNEL_GAS_CONSUMPTION_CURRENT_YEAR = "gasConsumptionCurrentYear";

    public static final String CHANNEL_POWER_CURRENT_ATTIC = "currentPowerAttic";
    public static final String CHANNEL_POWER_CURRENT_GROUNDFLOOR = "currentPowerGroundfloor";
    public static final String CHANNEL_POWER_CURRENT_TOTAL = "currentPowerTotal";
    public static final String CHANNEL_POWER_CURRENT_PHOTOVOLTAIC = "currentPowerPhotovoltaic";
    public static final String CHANNEL_POWER_CURRENT_BUY = "currentPowerBuy";
    public static final String CHANNEL_POWER_CURRENT_SELL = "currentPowerSell";

    public static final String CHANNEL_ENERGY_JANUARY_ATTIC = "energyJanuaryAttic";
    public static final String CHANNEL_ENERGY_FEBRUARY_ATTIC = "energyFebruaryAttic";
    public static final String CHANNEL_ENERGY_MARCH_ATTIC = "energyMarchAttic";
    public static final String CHANNEL_ENERGY_APRIL_ATTIC = "energyAprilAttic";
    public static final String CHANNEL_ENERGY_MAY_ATTIC = "energyMayAttic";
    public static final String CHANNEL_ENERGY_JUNE_ATTIC = "energyJuneAttic";
    public static final String CHANNEL_ENERGY_JULY_ATTIC = "energyJulyAttic";
    public static final String CHANNEL_ENERGY_AUGUST_ATTIC = "energyAugustAttic";
    public static final String CHANNEL_ENERGY_SEPTEMBER_ATTIC = "energySeptemberAttic";
    public static final String CHANNEL_ENERGY_OCTOBER_ATTIC = "energyOctoberAttic";
    public static final String CHANNEL_ENERGY_NOVEMBER_ATTIC = "energyNovemberAttic";
    public static final String CHANNEL_ENERGY_DECEMBER_ATTIC = "energyDecemberAttic";

    public static final String CHANNEL_ENERGY_JANUARY_GROUNDFLOOR = "energyJanuaryGroundfloor";
    public static final String CHANNEL_ENERGY_FEBRUARY_GROUNDFLOOR = "energyFebruaryGroundfloor";
    public static final String CHANNEL_ENERGY_MARCH_GROUNDFLOOR = "energyMarchGroundfloor";
    public static final String CHANNEL_ENERGY_APRIL_GROUNDFLOOR = "energyAprilGroundfloor";
    public static final String CHANNEL_ENERGY_MAY_GROUNDFLOOR = "energyMayGroundfloor";
    public static final String CHANNEL_ENERGY_JUNE_GROUNDFLOOR = "energyJuneGroundfloor";
    public static final String CHANNEL_ENERGY_JULY_GROUNDFLOOR = "energyJulyGroundfloor";
    public static final String CHANNEL_ENERGY_AUGUST_GROUNDFLOOR = "energyAugustGroundfloor";
    public static final String CHANNEL_ENERGY_SEPTEMBER_GROUNDFLOOR = "energySeptemberGroundfloor";
    public static final String CHANNEL_ENERGY_OCTOBER_GROUNDFLOOR = "energyOctoberGroundfloor";
    public static final String CHANNEL_ENERGY_NOVEMBER_GROUNDFLOOR = "energyNovemberGroundfloor";
    public static final String CHANNEL_ENERGY_DECEMBER_GROUNDFLOOR = "energyDecemberGroundfloor";

    public static final String CHANNEL_ENERGY_MONTH_CURRENT_ATTIC = "energyCurrentMonthAttic";
    public static final String CHANNEL_ENERGY_YEAR_CURRENT_ATTIC = "energyCurrentYearAttic";
    public static final String CHANNEL_ENERGY_PERCENTAGE_SHARE_YEAR_ATTIC = "energyPercentageShareYearAttic";

    public static final String CHANNEL_ENERGY_MONTH_CURRENT_GROUNDFLOOR = "energyCurrentMonthGroundfloor";
    public static final String CHANNEL_ENERGY_YEAR_CURRENT_GROUNDFLOOR = "energyCurrentYearGroundfloor";
    public static final String CHANNEL_ENERGY_PERCENTAGE_SHARE_YEAR_GROUNDFLOOR = "energyPercentageShareYearGroundfloor";
}
