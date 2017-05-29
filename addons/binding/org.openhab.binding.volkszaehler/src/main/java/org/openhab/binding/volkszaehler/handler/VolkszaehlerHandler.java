/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler.handler;

import static org.openhab.binding.volkszaehler.VolkszaehlerBindingConstants.*;

import java.math.BigDecimal;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.volkszaehler.internal.MySQLReader;
import org.openhab.binding.volkszaehler.internal.SQLReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VolkszaehlerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class VolkszaehlerHandler extends BaseThingHandler implements SQLReaderListener {

    private Logger logger = LoggerFactory.getLogger(VolkszaehlerHandler.class);

    private MySQLReader mySqlReader;

    public VolkszaehlerHandler(Thing thing) {
        super(thing);
    }

    /**
     * Returns the IP Address of this device
     *
     * @return the IP Address of this device
     */
    public String getIPAddress() {
        return (String) getThing().getConfiguration().getProperties().get(DEVICE_PARAMETER_HOST);
    }

    /**
     * Returns the database name of the sql database
     *
     * @return the database name of the sql database
     */
    public String getDBName() {
        return (String) getThing().getConfiguration().getProperties().get(DEVICE_PARAMETER_DB_NAME);
    }

    /**
     * Returns the username of the sql database
     *
     * @return the username of the sql database
     */
    public String getUserName() {
        return (String) getThing().getConfiguration().getProperties().get(DEVICE_PARAMETER_USERNAME);
    }

    /**
     * Returns the password of the sql database
     *
     * @return the password of the sql database
     */
    public String getPassword() {
        return (String) getThing().getConfiguration().getProperties().get(DEVICE_PARAMETER_PASSWORD);
    }

    public long getRefreshInterval() {
        long timeInMintues = ((BigDecimal) thing.getConfiguration().get(DEVICE_PARAMETER_REFRESH)).longValue();
        long timeInSeconds = timeInMintues * 60;
        return timeInSeconds;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        try {
            mySqlReader = new MySQLReader(getIPAddress(), getDBName(), getUserName(), getPassword());
            mySqlReader.addListener(this);
            scheduler.scheduleWithFixedDelay(mySqlReader, 1, getRefreshInterval(), TimeUnit.SECONDS);
            updateStatus(ThingStatus.ONLINE);
        } catch (ClassNotFoundException e) {
            logger.error("Error during loading drivers for database");
            mySqlReader.removeListener(this);
            mySqlReader = null;
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void refreshValues() {
        updateState(CHANNEL_BUFFER_TEMPERATURE_AVERAGE, mySqlReader.buffer.getTemperatureAverage());
        updateState(CHANNEL_BUFFER_TEMPERATURE_BOTTOM, mySqlReader.buffer.getTemperatureBottom());
        updateState(CHANNEL_BUFFER_TEMPERATURE_MIDDLE, mySqlReader.buffer.getTemperatureMiddle());
        updateState(CHANNEL_BUFFER_TEMPERATURE_TOP, mySqlReader.buffer.getTemperatureTop());

        updateState(CHANNEL_HOT_WATER_TEMPERATURE, mySqlReader.hotWater.getTemperature());

        updateState(CHANNEL_GAS_CONSUMPTION, mySqlReader.gas.getCurrentConsumption());
        updateState(CHANNEL_GAS_THERME_RUNNING, mySqlReader.gas.isThermeRunning());
        updateState(CHANNEL_GAS_CONSUMPTION_CURRENT_MONTH, mySqlReader.gas.getConsumptionCurrentMonth());
        updateState(CHANNEL_GAS_CONSUMPTION_CURRENT_YEAR, mySqlReader.gas.getConsumptionCurrentYear());

        updateState(CHANNEL_POWER_CURRENT_ATTIC, mySqlReader.power.getConsumptionAttic());// TODO
        updateState(CHANNEL_POWER_CURRENT_GROUNDFLOOR, mySqlReader.power.getConsumptionGroundfloor());// TODO
        updateState(CHANNEL_POWER_CURRENT_TOTAL, mySqlReader.power.getConsumption());
        updateState(CHANNEL_POWER_CURRENT_PHOTOVOLTAIC, mySqlReader.power.getPhotovoltaic());
        updateState(CHANNEL_POWER_CURRENT_BUY, mySqlReader.power.getBuy());
        updateState(CHANNEL_POWER_CURRENT_SELL, mySqlReader.power.getSell());

        updateState(CHANNEL_ENERGY_JANUARY_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.JANUARY));
        updateState(CHANNEL_ENERGY_FEBRUARY_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.FEBRUARY));
        updateState(CHANNEL_ENERGY_MARCH_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.MARCH));
        updateState(CHANNEL_ENERGY_APRIL_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.APRIL));
        updateState(CHANNEL_ENERGY_MAY_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.MAY));
        updateState(CHANNEL_ENERGY_JUNE_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.JUNE));
        updateState(CHANNEL_ENERGY_JULY_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.JULY));
        updateState(CHANNEL_ENERGY_AUGUST_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.AUGUST));
        updateState(CHANNEL_ENERGY_SEPTEMBER_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.SEPTEMBER));
        updateState(CHANNEL_ENERGY_OCTOBER_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.OCTOBER));
        updateState(CHANNEL_ENERGY_NOVEMBER_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.NOVEMBER));
        updateState(CHANNEL_ENERGY_DECEMBER_ATTIC, mySqlReader.energyAttic.getEnergyMonth(Month.DECEMBER));

        updateState(CHANNEL_ENERGY_JANUARY_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.JANUARY));
        updateState(CHANNEL_ENERGY_FEBRUARY_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.FEBRUARY));
        updateState(CHANNEL_ENERGY_MARCH_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.MARCH));
        updateState(CHANNEL_ENERGY_APRIL_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.APRIL));
        updateState(CHANNEL_ENERGY_MAY_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.MAY));
        updateState(CHANNEL_ENERGY_JUNE_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.JUNE));
        updateState(CHANNEL_ENERGY_JULY_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.JULY));
        updateState(CHANNEL_ENERGY_AUGUST_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.AUGUST));
        updateState(CHANNEL_ENERGY_SEPTEMBER_GROUNDFLOOR,
                mySqlReader.energyGroundfloor.getEnergyMonth(Month.SEPTEMBER));
        updateState(CHANNEL_ENERGY_OCTOBER_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.OCTOBER));
        updateState(CHANNEL_ENERGY_NOVEMBER_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.NOVEMBER));
        updateState(CHANNEL_ENERGY_DECEMBER_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyMonth(Month.DECEMBER));

        updateState(CHANNEL_ENERGY_MONTH_CURRENT_ATTIC, mySqlReader.energyAttic.getEnergyCurrentMonth());
        updateState(CHANNEL_ENERGY_YEAR_CURRENT_ATTIC, mySqlReader.energyAttic.getEnergyCurrentYear());
        updateState(CHANNEL_ENERGY_PERCENTAGE_SHARE_YEAR_ATTIC, mySqlReader.energyAttic
                .getEnergyPercentageShareYear(mySqlReader.energyGroundfloor.getEnergyCurrentYear()));

        updateState(CHANNEL_ENERGY_MONTH_CURRENT_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyCurrentMonth());
        updateState(CHANNEL_ENERGY_YEAR_CURRENT_GROUNDFLOOR, mySqlReader.energyGroundfloor.getEnergyCurrentYear());
        updateState(CHANNEL_ENERGY_PERCENTAGE_SHARE_YEAR_GROUNDFLOOR, mySqlReader.energyGroundfloor
                .getEnergyPercentageShareYear(mySqlReader.energyAttic.getEnergyCurrentYear()));
    }

}
