// package org.openhab.binding.volkszaehler.internal;
//
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
//
// public class Test {
//
// public static DataSet dataSet;
//
// public static void main(String[] args) throws SQLException, IntervalException, IOException, FutureErrorException {
// // dataSet = readDataFromCSVFile("C:/Users/thoma/Desktop/", "WG.csv");
// dataSet = readDataFromMySQL("jdbc:mysql://192.168.0.102/volkszaehler", "root", "benzin",
// new Time(2015, 12, 30, 0, 0, 0));
//
// dataSet.generateInterpolatedData(new Time(2016, 10, 1, 0, 0, 0), new Time(2016, 10, 31, 23, 59, 0));
// System.out.println(calcEnergyIn_kWh());
// writeData("C:/Users/thoma/Desktop/", "WG_neu.csv");
// }
//
// public static DataSet readDataFromCSVFile(String path, String csvFile) throws IOException {
// DataSet dataSet = new DataSet();
// BufferedReader reader = new BufferedReader(new FileReader(new File(path, csvFile)));
//
// String line;
// if (reader.ready()) {
// line = reader.readLine();
// }
//
// while (reader.ready()) {
// line = reader.readLine();
// String[] tempList = line.split(",");
// dataSet.addData(tempList[0], Double.parseDouble(tempList[1]));
// }
// reader.close();
//
// return dataSet;
// }
//
// public static DataSet readDataFromMySQL(String url, String user, String password, Time time) throws SQLException {
// StringBuffer buffer = new StringBuffer();
// buffer.append("SELECT value,timestamp FROM data WHERE channel_id=10 AND timestamp>");
// buffer.append(time.getTimeInMillis());
// return readDataFromMySQL(url, user, password, buffer.toString());
// }
//
// public static DataSet readDataFromMySQL(String url, String user, String password) throws SQLException {
// return readDataFromMySQL(url, user, password, "SELECT value,timestamp FROM data WHERE channel_id=10");
// }
//
// public static DataSet readDataFromMySQL(String url, String user, String password, String sql) throws SQLException {
// DataSet dataSet = new DataSet();
//
// Connection connection = DriverManager.getConnection(url, user, password);
// Statement stmt = connection.createStatement();
// ResultSet resultSet = stmt.executeQuery(sql);
//
// while (resultSet.next()) {
// long timestamp = resultSet.getLong("timestamp");
// Double energy = resultSet.getDouble("value");
//
// dataSet.addData(new Time(timestamp), energy);
// }
// resultSet.close();
// connection.close();
//
// return dataSet;
// }
//
// public static double calcEnergyIn_kWh() throws FutureErrorException {
// Time firstTime = dataSet.getIterpolatedTimeAt(0);
// Time lastTime = dataSet.getIterpolatedTimeAt(dataSet.getNumberofGeneratedValues() - 1);
// double diffInHours = Time.calDiffInHours(firstTime, lastTime);
// double value = 0;
// for (int timeStep = 0; timeStep < dataSet.getNumberofGeneratedValues(); ++timeStep) {
// value = value + dataSet.getIterpolatedValueAt(timeStep);
// }
// value = value / 60;
// value = value / 1000;
// return value;
// }
//
// public static void writeData(String path, String csvFile) throws IOException, FutureErrorException {
// BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path, csvFile)));
//
// writer.write("Time,Value");
// writer.newLine();
//
// for (int timeStep = 0; timeStep < dataSet.getNumberofGeneratedValues(); ++timeStep) {
// Time time = dataSet.getIterpolatedTimeAt(timeStep);
// double value = dataSet.getIterpolatedValueAt(timeStep);
// writer.write(time.toString() + ",");
// writer.write(value + "");
// writer.newLine();
// }
// writer.close();
// }
//
// }
