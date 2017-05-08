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

    private ArrayList<Double> listOfEnergyMonthAttic;
    private ArrayList<Double> listOfEnergyMonthGroundfloor;

    private ArrayList<Double> listOfGasConsumption;

    public MySQLReader(String host, String dbName, String user, String password)
            throws ClassNotFoundException, SQLException {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.listOfListener = new ArrayList<SQLReaderListener>();
        this.listOfEnergyMonthAttic = new ArrayList<Double>();
        this.listOfEnergyMonthGroundfloor = new ArrayList<Double>();
        this.listOfGasConsumption = new ArrayList<Double>();

        Class.forName("com.mysql.jdbc.Driver");
    }

    @Override
    public void run() {
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
        callAllListener();
    }

    @Override
    public void open() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbName, user, password);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
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
        for (SQLReaderListener listener : listOfListener) {
            listener.refreshValues();
        }
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

    private double calcEnergyMonthAttic(int month) throws SQLException, FutureErrorException, IntervalException {
        return calcEnergyMonth(10, month);
    }

    private double calcEnergyMonthGroundfloor(int month) throws SQLException, FutureErrorException, IntervalException {
        return calcEnergyMonth(13, month);
    }

    private double calcGasConsumption(int month) throws SQLException {
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

        double gas = 0;
        for (int timeStep = 0; timeStep < dataSet.getNumberofOriginalValues(); ++timeStep) {
            gas = gas + dataSet.getOriginalValue(timeStep);
        }
        gas = gas / 10;
        return gas;
    }

    private double calcEnergyMonth(int channelID, int month)
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

        double energy = 0;
        for (int timeStep = 0; timeStep < dataSet.getNumberofGeneratedValues(); ++timeStep) {
            energy = energy + dataSet.getIterpolatedValueAt(timeStep);
        }
        energy = energy / 60;
        energy = energy / 1000;
        return energy;
    }

    public DecimalType getBufferTemperatureAverage() {
        if (bufferTemperatureAverage != null) {
            return new DecimalType(bufferTemperatureAverage);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getBufferTemperatureBottom() {
        if (bufferTemperatureBottom != null) {
            return new DecimalType(bufferTemperatureBottom);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getBufferTemperatureMiddle() {
        if (bufferTemperatureMiddle != null) {
            return new DecimalType(bufferTemperatureMiddle);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getBufferTemperatureTop() {
        if (bufferTemperatureTop != null) {
            return new DecimalType(bufferTemperatureTop);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getHotWaterTemperature() {
        if (hotWaterTemperature != null) {
            return new DecimalType(hotWaterTemperature);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerAttic() {
        if (currentPowerAttic != null) {
            return new DecimalType(currentPowerAttic);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerGroundfloor() {
        if (currentPowerGroundfloor != null) {
            return new DecimalType(currentPowerGroundfloor);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerPV() {
        if (currentPowerPV != null) {
            return new DecimalType(currentPowerPV);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerBuy() {
        if (currentPowerBuy != null) {
            return new DecimalType(currentPowerBuy);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerSell() {
        if (currentPowerSell != null) {
            return new DecimalType(currentPowerSell);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getCurrentPowerTotal() {
        if ((currentPowerAttic != null) || (currentPowerGroundfloor != null)) {
            return new DecimalType(currentPowerAttic + currentPowerGroundfloor);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyCurrentMonthAttic() {
        int index = new Time().getMonth() - 1;
        if (listOfEnergyMonthAttic.size() > index + 1) {
            return new DecimalType(listOfEnergyMonthAttic.get(new Time().getMonth() - 1));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyCurrentMonthGroundfloor() {
        int index = new Time().getMonth() - 1;
        if (listOfEnergyMonthGroundfloor.size() > index + 1) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(index));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getGasConsumptionCurrentMonth() {
        int index = new Time().getMonth() - 1;
        if (listOfGasConsumption.size() > index + 1) {
            return new DecimalType(listOfGasConsumption.get(index));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyCurrentYearAttic() {
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfEnergyMonthAttic.size() > i + 1) {
                energy = energy + listOfEnergyMonthAttic.get(i);
            }
        }
        return new DecimalType(energy);
    }

    public DecimalType getEnergyCurrentYearGroundfloor() {
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfEnergyMonthGroundfloor.size() > i + 1) {
                energy = energy + listOfEnergyMonthGroundfloor.get(i);
            }
        }
        return new DecimalType(energy);
    }

    public DecimalType getGasConsumptionCurrentYear() {
        double energy = 0;
        for (int i = 0; i < 12; i++) {
            if (listOfGasConsumption.size() > i + 1) {
                energy = energy + listOfGasConsumption.get(i);
            }
        }
        return new DecimalType(energy);
    }

    public DecimalType getEnergyPercentageShareYearAttic() {
        double energyYear = getEnergyCurrentYearAttic().doubleValue() + getEnergyCurrentYearGroundfloor().doubleValue();
        if (energyYear > 0) {
            return new DecimalType((getEnergyCurrentYearAttic().doubleValue() / energyYear) * 100);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyPercentageShareYearGroundfloor() {
        double energyYear = getEnergyCurrentYearAttic().doubleValue() + getEnergyCurrentYearGroundfloor().doubleValue();
        if (energyYear > 0) {
            return new DecimalType((getEnergyCurrentYearGroundfloor().doubleValue() / energyYear) * 100);
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJanuaryAttic() {
        if (listOfEnergyMonthAttic.size() > 0) {
            return new DecimalType(listOfEnergyMonthAttic.get(0));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyFebruaryAttic() {
        if (listOfEnergyMonthAttic.size() > 1) {
            return new DecimalType(listOfEnergyMonthAttic.get(1));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyMarchAttic() {
        if (listOfEnergyMonthAttic.size() > 2) {
            return new DecimalType(listOfEnergyMonthAttic.get(2));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyAprilAttic() {
        if (listOfEnergyMonthAttic.size() > 3) {
            return new DecimalType(listOfEnergyMonthAttic.get(3));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyMayAttic() {
        if (listOfEnergyMonthAttic.size() > 4) {
            return new DecimalType(listOfEnergyMonthAttic.get(4));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJuneAttic() {
        if (listOfEnergyMonthAttic.size() > 5) {
            return new DecimalType(listOfEnergyMonthAttic.get(5));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJulyAttic() {
        if (listOfEnergyMonthAttic.size() > 6) {
            return new DecimalType(listOfEnergyMonthAttic.get(6));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyAugustAttic() {
        if (listOfEnergyMonthAttic.size() > 7) {
            return new DecimalType(listOfEnergyMonthAttic.get(7));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergySeptemberAttic() {
        if (listOfEnergyMonthAttic.size() > 8) {
            return new DecimalType(listOfEnergyMonthAttic.get(8));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyOctoberAttic() {
        if (listOfEnergyMonthAttic.size() > 9) {
            return new DecimalType(listOfEnergyMonthAttic.get(9));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyNovemberAttic() {
        if (listOfEnergyMonthAttic.size() > 10) {
            return new DecimalType(listOfEnergyMonthAttic.get(10));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyDecemberAttic() {
        if (listOfEnergyMonthAttic.size() > 11) {
            return new DecimalType(listOfEnergyMonthAttic.get(11));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJanuaryGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 0) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(0));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyFebruaryGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 1) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(1));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyMarchGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 2) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(2));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyAprilGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 3) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(3));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyMayGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 4) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(4));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJuneGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 5) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(5));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyJulyGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 6) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(6));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyAugustGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 7) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(7));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergySeptemberGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 8) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(8));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyOctoberGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 9) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(9));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyNovemberGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 10) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(10));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getEnergyDecemberGroundfloor() {
        if (listOfEnergyMonthGroundfloor.size() > 11) {
            return new DecimalType(listOfEnergyMonthGroundfloor.get(11));
        }
        return new DecimalType(0.0);
    }

    public DecimalType getGasConsumptionCurrent() {
        if (gasConsumption != null) {
            return new DecimalType(gasConsumption);
        }
        return new DecimalType(0.0);
    }

    public OnOffType getGastothermRunning() {
        if (gastothermRunning != null) {
            if (gastothermRunning) {
                return OnOffType.ON;
            } else {
                return OnOffType.OFF;
            }
        }
        return OnOffType.OFF;
    }
}
