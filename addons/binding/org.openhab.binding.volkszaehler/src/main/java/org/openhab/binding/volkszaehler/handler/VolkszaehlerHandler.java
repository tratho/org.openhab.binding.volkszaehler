/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler.handler;

import static org.openhab.binding.volkszaehler.VolkszaehlerBindingConstants.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingFactory;
import org.eclipse.smarthome.core.thing.type.TypeResolver;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.volkszaehler.internal.MySQLReader;
import org.openhab.binding.volkszaehler.internal.MySQLReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VolkszaehlerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class VolkszaehlerHandler extends BaseThingHandler implements MySQLReaderListener {

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
        return ((BigDecimal) thing.getConfiguration().get(DEVICE_PARAMETER_REFRESH)).longValue();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        mySqlReader = new MySQLReader(getIPAddress(), getDBName(), getUserName(), getPassword());
        mySqlReader.addListener(this);
        mySqlReader.setThing(thing);
        try {
            mySqlReader.tryConnect();
            updateStatus(ThingStatus.ONLINE);
            scheduler.scheduleWithFixedDelay(mySqlReader, 300, getRefreshInterval(), TimeUnit.MILLISECONDS);
        } catch (SQLException e) {
            logger.error(thing + ": Error during opening database ", e);
            updateStatus(ThingStatus.OFFLINE);
            mySqlReader.removeListener(this);
            mySqlReader = null;
        }
    }

    private ChannelUID getChannelUID(String channelId) {
        Channel channel = thing.getChannel(channelId);
        if (channel == null) {
            // refresh thing...
            Thing newThing = ThingFactory.createThing(TypeResolver.resolve(thing.getThingTypeUID()), thing.getUID(),
                    thing.getConfiguration());
            updateThing(newThing);
            channel = thing.getChannel(channelId);
        }
        return channel.getUID();
    }

    @Override
    protected void updateState(String id, State state) {
        super.updateState(getChannelUID(id), state);
    }

    @Override
    public void getUpdate() {
        updateState(CHANNEL_BUFFER_TEMPERATURE_AVERAGE, mySqlReader.getBufferTemperatureAverage());
        updateState(CHANNEL_BUFFER_TEMPERATURE_BOTTOM, mySqlReader.getBufferTemperatureBottom());
        updateState(CHANNEL_BUFFER_TEMPERATURE_MIDDLE, mySqlReader.getBufferTemperatureMiddle());
        updateState(CHANNEL_BUFFER_TEMPERATURE_TOP, mySqlReader.getBufferTemperatureTop());

        updateState(CHANNEL_CURRENT_POWER_ATTIC, mySqlReader.getCurrentPowerAttic());
        updateState(CHANNEL_CURRENT_POWER_GROUNDFLOOR, mySqlReader.getCurrentPowerGroundfloor());
        updateState(CHANNEL_CURRENT_POWER_TOTAL, mySqlReader.getCurrentPowerTotal());
        updateState(CHANNEL_CURRENT_POWER_PHOTOVOLTAIC, mySqlReader.getCurrentPowerPV());
        updateState(CHANNEL_CURRENT_POWER_BUY, mySqlReader.getCurrentPowerBuy());
        updateState(CHANNEL_CURRENT_POWER_SELL, mySqlReader.getCurrentPowerSell());

        updateState(CHANNEL_CURRENT_MONTH_ENERGY_ATTIC, mySqlReader.getEnergyCurrentMonthAttic());
        updateState(CHANNEL_CURRENT_MONTH_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyCurrentMonthGroundfloor());

        updateState(CHANNEL_CURRENT_YEAR_ENERGY_ATTIC, mySqlReader.getEnergyCurrentYearAttic());
        updateState(CHANNEL_CURRENT_YEAR_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyCurrentYearGroundfloor());

        updateState(CHANNEL_PERCENTAGE_SHARE_YEAR_ENERGY_ATTIC, mySqlReader.getEnergyPercentageShareYearAttic());
        updateState(CHANNEL_PERCENTAGE_SHARE_YEAR_ENERGY_GROUNDFLOOR,
                mySqlReader.getEnergyPercentageShareYearGroundfloor());

        updateState(CHANNEL_MONTH1_ENERGY_ATTIC, mySqlReader.getEnergyJanuaryAttic());
        updateState(CHANNEL_MONTH2_ENERGY_ATTIC, mySqlReader.getEnergyFebruaryAttic());
        updateState(CHANNEL_MONTH3_ENERGY_ATTIC, mySqlReader.getEnergyMarchAttic());
        updateState(CHANNEL_MONTH4_ENERGY_ATTIC, mySqlReader.getEnergyAprilAttic());
        updateState(CHANNEL_MONTH5_ENERGY_ATTIC, mySqlReader.getEnergyMayAttic());
        updateState(CHANNEL_MONTH6_ENERGY_ATTIC, mySqlReader.getEnergyJuneAttic());
        updateState(CHANNEL_MONTH7_ENERGY_ATTIC, mySqlReader.getEnergyJulyAttic());
        updateState(CHANNEL_MONTH8_ENERGY_ATTIC, mySqlReader.getEnergyAugustAttic());
        updateState(CHANNEL_MONTH9_ENERGY_ATTIC, mySqlReader.getEnergySeptemberAttic());
        updateState(CHANNEL_MONTH10_ENERGY_ATTIC, mySqlReader.getEnergyOctoberAttic());
        updateState(CHANNEL_MONTH11_ENERGY_ATTIC, mySqlReader.getEnergyNovemberAttic());
        updateState(CHANNEL_MONTH12_ENERGY_ATTIC, mySqlReader.getEnergyDecemberAttic());

        updateState(CHANNEL_MONTH1_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyJanuaryGroundfloor());
        updateState(CHANNEL_MONTH2_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyFebruaryGroundfloor());
        updateState(CHANNEL_MONTH3_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyMarchGroundfloor());
        updateState(CHANNEL_MONTH4_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyAprilGroundfloor());
        updateState(CHANNEL_MONTH5_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyMayGroundfloor());
        updateState(CHANNEL_MONTH6_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyJuneGroundfloor());
        updateState(CHANNEL_MONTH7_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyJulyGroundfloor());
        updateState(CHANNEL_MONTH8_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyAugustGroundfloor());
        updateState(CHANNEL_MONTH9_ENERGY_GROUNDFLOOR, mySqlReader.getEnergySeptemberGroundfloor());
        updateState(CHANNEL_MONTH10_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyOctoberGroundfloor());
        updateState(CHANNEL_MONTH11_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyNovemberGroundfloor());
        updateState(CHANNEL_MONTH12_ENERGY_GROUNDFLOOR, mySqlReader.getEnergyDecemberGroundfloor());

        updateState(CHANNEL_HOT_WATER_TEMPERATURE, mySqlReader.getHotWaterTemperature());

        updateState(CHANNEL_GAS_CONSUMPTION, mySqlReader.getGasConsumptionCurrent());
        updateState(CHANNEL_GASTOTHERM_RUNNING, mySqlReader.getGastothermRunning());

        updateState(CHANNEL_GAS_CONSUMPTION_CURRENT_MONTH, mySqlReader.getGasConsumptionCurrentMonth());
        updateState(CHANNEL_GAS_CONSUMPTION_CURRENT_YEAR, mySqlReader.getGasConsumptionCurrentYear());
    }

}
