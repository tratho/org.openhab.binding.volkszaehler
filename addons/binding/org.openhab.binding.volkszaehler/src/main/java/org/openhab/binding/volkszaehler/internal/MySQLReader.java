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
import java.util.List;

import org.openhab.binding.volkszaehler.internal.data.Buffer;
import org.openhab.binding.volkszaehler.internal.data.Energy;
import org.openhab.binding.volkszaehler.internal.data.Gas;
import org.openhab.binding.volkszaehler.internal.data.HotWater;
import org.openhab.binding.volkszaehler.internal.data.Power;
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

    public Buffer buffer;
    public Gas gas;
    public HotWater hotWater;
    public Power power;
    public Energy energyGroundfloor;
    public Energy energyAttic;

    public MySQLReader(String host, String dbName, String user, String password) throws ClassNotFoundException {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.listOfListener = new ArrayList<>();

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
        logger.debug("callAllListener()");
        try {
            open();

            pullBuffer();
            pullGas();
            pullHotWater();
            pullPowerGeneral();
            pullEnergyAttic();
            pullEnergyGroundfloor();

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

    private void pullBuffer() {
        ResultSet resultSet = null;

        Double bufferTemperatureBottom = null;
        Double bufferTemperatureMiddle = null;
        Double bufferTemperatureTop = null;

        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=19 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            resultSet = null;
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                bufferTemperatureBottom = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                bufferTemperatureBottom = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=24 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            } catch (SQLException e1) {
                resultSet = null;
            }
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                bufferTemperatureMiddle = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                bufferTemperatureMiddle = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=22 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            } catch (SQLException e1) {
                resultSet = null;
            }
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                bufferTemperatureTop = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                bufferTemperatureTop = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        buffer = new Buffer(bufferTemperatureBottom, bufferTemperatureMiddle, bufferTemperatureTop);
    }

    private void pullGas() {
        ResultSet resultSet;

        Double currentGasConsumption = null;
        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=48 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            resultSet = null;
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                currentGasConsumption = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                currentGasConsumption = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        List<Double> listOfGasConsumption = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            try {
                listOfGasConsumption.add(calcGasConsumption(i));
            } catch (SQLException e) {
                listOfGasConsumption.add(null);
            }
        }

        gas = new Gas(currentGasConsumption, listOfGasConsumption);
    }

    private void pullHotWater() {
        ResultSet resultSet;

        Double hotWaterTemperature = null;
        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=25 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            resultSet = null;
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                hotWaterTemperature = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                hotWaterTemperature = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        hotWater = new HotWater(hotWaterTemperature);
    }

    private void pullPowerGeneral() {
        ResultSet resultSet;

        Double currentPowerAttic = null;
        Double currentPowerGroundfloor = null;
        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=10 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            resultSet = null;
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                currentPowerAttic = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                currentPowerAttic = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=13 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            } catch (SQLException e1) {
                resultSet = null;
            }
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                currentPowerGroundfloor = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                currentPowerGroundfloor = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        Double currentPowerPV = null;
        try {
            resultSet = getResultSet("SELECT value FROM data WHERE channel_id=30 ORDER BY id DESC LIMIT 1");
        } catch (SQLException e) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            } catch (SQLException e1) {
                resultSet = null;
            }
        }
        if (resultSet != null) {
            try {
                resultSet.next();
                currentPowerPV = resultSet.getDouble("value");
                resultSet.close();
            } catch (SQLException e) {
                currentPowerPV = null;
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    resultSet = null;
                }
            }
        }

        power = new Power(currentPowerGroundfloor, currentPowerAttic, currentPowerPV);
    }

    private void pullEnergyAttic() {
        List<Double> listOfEnergyMonthAttic = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            try {
                listOfEnergyMonthAttic.add(calcEnergyMonthAttic(i));
            } catch (SQLException | FutureErrorException | IntervalException e) {
                listOfEnergyMonthAttic.add(null);
            }
        }
        energyAttic = new Energy(listOfEnergyMonthAttic);
    }

    private void pullEnergyGroundfloor() {
        List<Double> listOfEnergyMonthGroundfloor = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            try {
                listOfEnergyMonthGroundfloor.add(calcEnergyMonthGroundfloor(i));
            } catch (SQLException | FutureErrorException | IntervalException e) {
                listOfEnergyMonthGroundfloor.add(null);
            }
        }
        energyGroundfloor = new Energy(listOfEnergyMonthGroundfloor);
    }

    private Double calcEnergyMonthAttic(int month) throws SQLException, FutureErrorException, IntervalException {
        int channelNumberOnSQLServer = 10;
        return calcEnergyMonth(channelNumberOnSQLServer, month);
    }

    private Double calcEnergyMonthGroundfloor(int month) throws SQLException, FutureErrorException, IntervalException {
        int channelNumberOnSQLServer = 13;
        return calcEnergyMonth(channelNumberOnSQLServer, month);
    }

    private Double calcGasConsumption(int month) throws SQLException {
        Time startTimeForSQL = Time.getFirstOfMonth(month);
        Time endTimeForSQL = Time.getLastOfMonth(month);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT value, timestamp FROM data WHERE channel_id=48 AND timestamp>");
        sb.append(startTimeForSQL.getTimeInMillis());
        sb.append(" AND timestamp<");
        sb.append(endTimeForSQL.getTimeInMillis());
        ResultSet resultSet = getResultSet(sb.toString());

        DataSet dataSet = new DataSet();
        while (resultSet.next()) {
            long timestamp = resultSet.getLong("timestamp");
            Double power = resultSet.getDouble("value");

            dataSet.addData(new Time(timestamp), power);
        }
        resultSet.close();

        Double gas = null;
        for (int timeStep = 0; timeStep < dataSet.getNumberofOriginalValues(); ++timeStep) {
            if (gas == null) {
                gas = dataSet.getOriginalValue(timeStep);
            } else {
                gas += dataSet.getOriginalValue(timeStep);
            }
        }
        if (gas != null) {
            gas /= 10;
        }
        return gas;
    }

    private Double calcEnergyMonth(int channelID, int month)
            throws SQLException, FutureErrorException, IntervalException {
        Time startTimeForSQL = Time.getFirstOfMonth(month);
        Time endTimeForSQL = Time.getLastOfMonth(month);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT value,timestamp FROM data WHERE channel_id=");
        sb.append(channelID);
        sb.append(" AND timestamp>");
        sb.append(startTimeForSQL.getTimeInMillis());
        sb.append(" AND timestamp<");
        sb.append(endTimeForSQL.getTimeInMillis());
        ResultSet resultSet = getResultSet(sb.toString());

        DataSet dataSet = new DataSet();
        while (resultSet.next()) {
            long timestamp = resultSet.getLong("timestamp");
            Double power = resultSet.getDouble("value");

            dataSet.addData(new Time(timestamp), power);
        }
        resultSet.close();

        dataSet.generateInterpolatedData(Time.getFirstOfMonth(month), Time.getLastOfMonth(month));

        Double energy = null;
        for (int timeStep = 0; timeStep < dataSet.getNumberofGeneratedValues(); ++timeStep) {
            if (energy == null) {
                energy = dataSet.getIterpolatedValueAt(timeStep);
            } else {
                energy += dataSet.getIterpolatedValueAt(timeStep);
            }
        }
        if (energy != null) {
            energy /= 60;
            energy /= 1000;
        }
        return energy;
    }
}
