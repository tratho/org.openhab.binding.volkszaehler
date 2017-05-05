package org.openhab.binding.volkszaehler.internal;

import java.util.ArrayList;
import java.util.Calendar;

public class DataSet {
    private SplineInterpolation spline;

    private ArrayList<Long> listOfTimeOriginal;
    private ArrayList<Double> listOfValuesOriginal;

    public ArrayList<Time> listOfTimeInterpolated;
    public ArrayList<Double> listOfValuesInterpolated;

    private boolean isSplineInit;
    private boolean isGenerateDataToWrite;

    public DataSet() {
        listOfTimeOriginal = new ArrayList<Long>();
        listOfValuesOriginal = new ArrayList<Double>();
        spline = new SplineInterpolation();
        listOfTimeInterpolated = new ArrayList<Time>();
        listOfValuesInterpolated = new ArrayList<Double>();
        isGenerateDataToWrite = false;
        isSplineInit = false;
    }

    public void addData(String dateAndTime, double value) {
        Time time = new Time(dateAndTime);
        addData(time, value);
    }

    public void addData(Time time, double value) {
        listOfTimeOriginal.add(time.getTimeInMillis());
        listOfValuesOriginal.add(value);
        isSplineInit = false;
        isGenerateDataToWrite = false;
        listOfTimeInterpolated.clear();
        listOfValuesInterpolated.clear();
    }

    public void addData(int index, Time time, double value) {
        listOfTimeOriginal.add(index, time.getTimeInMillis());
        listOfValuesOriginal.add(index, value);
        isSplineInit = false;
        isGenerateDataToWrite = false;
        listOfTimeInterpolated.clear();
        listOfValuesInterpolated.clear();
    }

    public double getValueAtTime(int year, int month, int day, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, min, sec);

        return getValueAtTime(new Time(cal.getTimeInMillis()));
    }

    public double getValueAtTime(Time time) {
        if (!isSplineInit) {
            initSpline();
        }

        return spline.getValue(time.getTimeInMillis());
    }

    public double getOriginalValue(int index) {
        return listOfValuesOriginal.get(index);
    }

    public Time getFirstTime() {
        if (listOfTimeOriginal.size() != 0) {
            return new Time(listOfTimeOriginal.get(0));
        } else {
            return null;
        }
    }

    public Time getLastTime() {
        return new Time(listOfTimeOriginal.get(listOfTimeOriginal.size() - 1));
    }

    public void generateInterpolatedData(Time startTime, Time endTime) throws IntervalException {
        Time firstTime = getFirstTime();
        if (firstTime != null) {
            long firstTimeMillis = firstTime.getTimeInMillis();
            long startTimeMillis = startTime.getTimeInMillis();
            long endTimeMillis = endTime.getTimeInMillis();

            if (endTimeMillis > getLastTime().getTimeInMillis()) {
                endTimeMillis = getLastTime().getTimeInMillis();
            }

            if (startTimeMillis < firstTimeMillis) {
                startTimeMillis = firstTimeMillis + 1;
            }

            if ((startTimeMillis < firstTimeMillis) || (startTimeMillis > endTimeMillis)) {
                throw new IntervalException("Start time is not in interval of Dataset");
            }

            Time time = startTime;
            while (time.getTimeInMillis() < endTimeMillis) {
                listOfTimeInterpolated.add(time);
                listOfValuesInterpolated.add(getValueAtTime(time));
                double minutesToAdd = 1;
                long newTimeInMillis = (long) (time.getTimeInMillis() + minutesToAdd * 60 * 1000);
                time = new Time(newTimeInMillis);
            }
            isGenerateDataToWrite = true;
        }
    }

    public void generateInterpolatedData(Time startTime) throws IntervalException {
        generateInterpolatedData(startTime, getLastTime());
    }

    public Time getIterpolatedTimeAt(int index) throws FutureErrorException {
        if (isGenerateDataToWrite == false) {
            throw new FutureErrorException("Generate interpolated data before");
        }
        return listOfTimeInterpolated.get(index);
    }

    public double getIterpolatedValueAt(int index) throws FutureErrorException {
        if (isGenerateDataToWrite == false) {
            throw new FutureErrorException("Generate interpolated data before");
        }
        return listOfValuesInterpolated.get(index);
    }

    public int getNumberofGeneratedValues() {
        return listOfTimeInterpolated.size();
    }

    public int getNumberofOriginalValues() {
        return listOfValuesOriginal.size();
    }

    private void initSpline() {
        spline.createSpline(listOfTimeOriginal, listOfValuesOriginal);
        isSplineInit = true;
    }

}
