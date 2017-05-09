/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volkszaehler.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MySQLReader}
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class MySQLReader implements Runnable, SQLReader {

    private Logger logger = LoggerFactory.getLogger(MySQLReader.class);

    private String host;
    private String dbName;
    private String user;
    private String password;
    private ArrayList<SQLReaderListener> listOfListener;

    private Connection connection;

    private Double bufferTemperatureAverage;
    private Double bufferTemperatureBottom;
    private Double bufferTemperatureMiddle;
    private Double bufferTemperatureTop;
    private Double hotWaterTemperature;
    private Double currentPowerAttic;
    private Double currentPowerGroundfloor;
    private Double currentPowerPV;
    private Double currentPowerBuy;
    private Double currentPowerSell;

    private Double gasConsumption;
    private Boolean gastothermRunning;

    private ArrayList<State> listOfEnergyMonthAttic;
    private ArrayList<State> listOfEnergyMonthGroundfloor;

    private ArrayList<State> listOfGasConsumption;

    public MySQLReader(String host, String dbName, String user, String password) throws ClassNotFoundException {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.listOfListener = new ArrayList<>();
        this.listOfEnergyMonthAttic = new ArrayList<>();
        this.listOfEnergyMonthGroundfloor = new ArrayList<>();
        this.listOfGasConsumption = new ArrayList<>();

        Class.forName("com.mysql.jdbc.Driver");
    }

    @Override
    public void run() {
        callAllListener();
    }

    @Override
    public ResultSet getResultSet(String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    @Override
    public void removeListener(SQLReaderListener listener) {
        for (int i = 0; i < listOfListener.size(); i++) {
            if (listOfListener.get(i) == listener) {
                listOfListener.remove(i);
            }
        }
        listOfListener.add(listener);
    }

    @Override
    public void addListener(SQLReaderListener listener) {
        listOfListener.add(listener);
    }

    @Override
    public void callAllListener() {
        try {
            open();
            try {
                pullCurrent();
            } catch (SQLException e) {
                logger.warn("Error during reading values form database");
            }
            try {
                pullEnergyAttic();
            } catch (SQLException | FutureErrorException | IntervalException e) {
                logger.warn("Error during reading values form database");
            }
            try {
                pullEnergyGroundfloor();
            } catch (SQLException | FutureErrorException | IntervalException e) {
                logger.warn("Error during reading values form database");
            }
            try {
                pullGasConsumption();
            } catch (SQLException e) {
                logger.warn("Error during reading values form database");
            }
            close();
            for (SQLReaderListener listener : listOfListener) {
                listener.refreshValues();
            }
        } catch (SQLException e) {
            logger.warn("Error during opening/closing database");
        }
    }

    private void open() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbName, user, password);
    }

    private void close() throws SQLException {
        connection.close();
    }

    private void pullCurrent() throws SQLException {
        ResultSet resultSet;

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=19 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        bufferTemperatureBottom = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=24 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        bufferTemperatureMiddle = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=22 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        bufferTemperatureTop = resultSet.getDouble("value");
        resultSet.close();

        bufferTemperatureAverage = (bufferTemperatureBottom + bufferTemperatureMiddle + bufferTemperatureTop) / 3;

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=25 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        hotWaterTemperature = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=10 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        currentPowerAttic = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=13 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        currentPowerGroundfloor = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=30 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        currentPowerPV = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=31 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        currentPowerBuy = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=33 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        currentPowerSell = resultSet.getDouble("value");
        resultSet.close();

        resultSet = getResultSet("SELECT value FROM data WHERE channel_id=48 ORDER BY id DESC LIMIT 1");
        resultSet.next();
        gasConsumption = resultSet.getDouble("value");
        gastothermRunning = gasConsumption > 0.0;
        resultSet.close();
    }

    private void pullEnergyAttic() throws SQLException, FutureErrorException, IntervalException {
        listOfEnergyMonthAttic.clear();
        for (int i = 1; i <= 12; i++) {
            listOfEnergyMonthAttic.add(calcEnergyMonthAttic(i));
        }
    }

    private void pullEnergyGroundfloor() throws SQLException, FutureErrorException, IntervalException {
        listOfEnergyMonthGroundfloor.clear();
        for (int i = 1; i <= 12; i++) {
            listOfEnergyMonthGroundfloor.add(calcEnergyMonthGroundfloor(i));
        }
    }

    private void pullGasConsumption() throws SQLException {
        listOfGasConsumption.clear();
        for (int i = 1; i <= 12; i++) {
            listOfGasConsumption.add(calcGasConsumption(i));
        }
    }

    private State calcEnergyMonthAttic(int month) throws SQLException, FutureErrorException, IntervalException {
        return calcEnergyMonth(10, month);
    }

    private State calcEnergyMonthGroundfloor(int month) throws SQLException, FutureErrorException, IntervalException {
        return calcEnergyMonth(13, month);
    }

    private State calcGasConsumption(int month) throws SQLException {
        Time startTimeForSQL = Time.getFirstOfMonth(month);
        Time endTimeForSQL = Time.getLastOfMonth(month);

        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT value,timestamp FROM data WHERE channel_id=");
        buffer.append(48);
        buffer.append(" AND timestamp>");
        buffer.append(startTimeForSQL.getTimeInMillis());
        buffer.append(" AND timestamp<");
        buffer.append(endTimeForSQL.getTimeInMillis());
        ResultSet resultSet = getResultSet(buffer.toString());

        DataSet dataSet = new DataSet();
        while (resultSet.next()) {
            long timestamp = resultSet.getLong("timestamp");
            Double power = resultSet.getDouble("value");

            dataSet.addData(new Time(timestamp), power);
        }
        resultSet.close();

        boolean checker = false;

        double gas = 0;
        for (int timeStep = 0; timeStep < dataSet.getNumberofOriginalValues(); ++timeStep) {
            gas += dataSet.getOriginalValue(timeStep);
            checker = true;
        }
        if (checker) {
            return new DecimalType(gas / 10);
        } else {
            return UnDefType.NULL;
        }
    }

    private State calcEnergyMonth(int channelID, int month)
            throws SQLException, FutureErrorException, IntervalException {
        Time startTimeForSQL = Time.getFirstOfMonth(month);
        Time endTimeForSQL = Time.getLastOfMonth(month);

        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT value,timestamp FROM data WHERE channel_id=");
        buffer.append(channelID);
        buffer.append(" AND timestamp>");
        buffer.append(startTimeForSQL.getTimeInMillis());
        buffer.append(" AND timestamp<");
        buffer.append(endTimeForSQL.getTimeInMillis());
        ResultSet resultSet = getResultSet(buffer.toString());

        DataSet dataSet = new DataSet();
        while (resultSet.next()) {
            long timestamp = resultSet.getLong("timestamp");
            Double power = resultSet.getDouble("value");

            dataSet.addData(new Time(timestamp), power);
        }
        resultSet.close();

        dataSet.generateInterpolatedData(Time.getFirstOfMonth(month), Time.getLastOfMonth(month));

        boolean checker = false;

        double energy = 0;
        for (int timeStep = 0; timeStep < dataSet.getNumberofGeneratedValues(); ++timeStep) {
            energy += dataSet.getIterpolatedValueAt(timeStep);
            checker = true;
        }
        energy = energy / 60;
        energy = energy / 1000;

        if (checker) {
            return new DecimalType(energy);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getBufferTemperatureAverage() {
        if (bufferTemperatureAverage != null) {
            return new DecimalType(bufferTemperatureAverage);
        }
        return UnDefType.NULL;
    }

    public State getBufferTemperatureBottom() {
        if (bufferTemperatureBottom != null) {
            return new DecimalType(bufferTemperatureBottom);
        }
        return UnDefType.NULL;
    }

    public State getBufferTemperatureMiddle() {
        if (bufferTemperatureMiddle != null) {
            return new DecimalType(bufferTemperatureMiddle);
        }
        return UnDefType.NULL;
    }

    public State getBufferTemperatureTop() {
        if (bufferTemperatureTop != null) {
            return new DecimalType(bufferTemperatureTop);
        }
        return UnDefType.NULL;
    }

    public State getHotWaterTemperature() {
        if (hotWaterTemperature != null) {
            return new DecimalType(hotWaterTemperature);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerAttic() {
        if (currentPowerAttic != null) {
            return new DecimalType(currentPowerAttic);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerGroundfloor() {
        if (currentPowerGroundfloor != null) {
            return new DecimalType(currentPowerGroundfloor);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerPV() {
        if (currentPowerPV != null) {
            return new DecimalType(currentPowerPV);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerBuy() {
        if (currentPowerBuy != null) {
            return new DecimalType(currentPowerBuy);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerSell() {
        if (currentPowerSell != null) {
            return new DecimalType(currentPowerSell);
        }
        return UnDefType.NULL;
    }

    public State getCurrentPowerTotal() {
        if ((currentPowerAttic != null) || (currentPowerGroundfloor != null)) {
            return new DecimalType(currentPowerAttic + currentPowerGroundfloor);
        }
        return UnDefType.NULL;
    }

    public State getEnergyCurrentMonthAttic() {
        int index = new Time().getMonth() - 1;
        return listOfEnergyMonthAttic.get(index);
    }

    public State getEnergyCurrentMonthGroundfloor() {
        int index = new Time().getMonth() - 1;
        return listOfEnergyMonthGroundfloor.get(index);
    }

    public State getGasConsumptionCurrentMonth() {
        int index = new Time().getMonth() - 1;
        if (listOfGasConsumption.size() > index + 1) {
            return listOfGasConsumption.get(index);
        }
        return UnDefType.NULL;
    }

    public State getEnergyCurrentYearAttic() {
        boolean checker = false;
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfEnergyMonthAttic.size() > i + 1) {
                State currentMonth = listOfEnergyMonthAttic.get(i);
                if (currentMonth instanceof DecimalType) {
                    energy += ((DecimalType) currentMonth).doubleValue();
                    checker = true;
                }
            }
        }
        if (checker) {
            return new DecimalType(energy);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getEnergyCurrentYearGroundfloor() {
        boolean checker = false;
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfEnergyMonthGroundfloor.size() > i + 1) {
                State currentMonth = listOfEnergyMonthGroundfloor.get(i);
                if (currentMonth instanceof DecimalType) {
                    energy += ((DecimalType) currentMonth).doubleValue();
                    checker = true;
                }
            }
        }
        if (checker) {
            return new DecimalType(energy);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getGasConsumptionCurrentYear() {
        boolean checker = false;
        double gas = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfGasConsumption.size() > i + 1) {
                State currentMonth = listOfGasConsumption.get(i);
                if (currentMonth instanceof DecimalType) {
                    gas += ((DecimalType) currentMonth).doubleValue();
                    checker = true;
                }
            }
        }
        if (checker) {
            return new DecimalType(gas);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getEnergyPercentageShareYearAttic() {
        double energyYear = ((DecimalType) getEnergyCurrentYearAttic()).doubleValue()
                + ((DecimalType) getEnergyCurrentYearGroundfloor()).doubleValue();
        if (energyYear > 0) {
            return new DecimalType((((DecimalType) getEnergyCurrentYearAttic()).doubleValue() / energyYear) * 100);
        }
        return UnDefType.NULL;
    }

    public State getEnergyPercentageShareYearGroundfloor() {
        double energyYear = ((DecimalType) getEnergyCurrentYearAttic()).doubleValue()
                + ((DecimalType) getEnergyCurrentYearGroundfloor()).doubleValue();
        if (energyYear > 0) {
            return new DecimalType(
                    (((DecimalType) getEnergyCurrentYearGroundfloor()).doubleValue() / energyYear) * 100);
        }
        return UnDefType.NULL;
    }

    public State getEnergyMonthAttic(int month) {
        return listOfEnergyMonthAttic.get(month - 1);
    }

    public State getEnergyMonthGroundfloor(int month) {
        return listOfEnergyMonthGroundfloor.get(month - 1);
    }

    public State getGasConsumptionCurrent() {
        if (gasConsumption != null) {
            return new DecimalType(gasConsumption);
        }
        return UnDefType.NULL;
    }

    public State getGastothermRunning() {
        if (gastothermRunning != null) {
            if (gastothermRunning) {
                return OnOffType.ON;
            } else {
                return OnOffType.OFF;
            }
        }
        return UnDefType.NULL;
    }
}
