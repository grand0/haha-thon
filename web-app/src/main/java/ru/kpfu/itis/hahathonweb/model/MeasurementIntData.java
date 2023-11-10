package ru.kpfu.itis.hahathonweb.model;

import java.sql.Timestamp;

public class MeasurementIntData {
    private long sensorId;
    private long measure;
    private int value;
    private Timestamp time;

    public MeasurementIntData(long sensorId, long measure, int value, Timestamp time) {
        this.sensorId = sensorId;
        this.measure = measure;
        this.value = value;
        this.time = time;
    }

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    public long getMeasure() {
        return measure;
    }

    public void setMeasure(long measure) {
        this.measure = measure;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
