package ru.kpfu.itis.hahathonweb.dto;

import java.sql.Timestamp;
import java.util.Objects;

public class MeasurementDto {
    private int value;
    private Timestamp time;

    public MeasurementDto(int value, Timestamp time) {
        this.value = value;
        this.time = time;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementDto that = (MeasurementDto) o;
        return getValue() == that.getValue() && Objects.equals(getTime(), that.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getTime());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
