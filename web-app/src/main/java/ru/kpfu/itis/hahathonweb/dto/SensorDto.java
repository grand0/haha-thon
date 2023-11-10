package ru.kpfu.itis.hahathonweb.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class SensorDto {
    private long id;
    private String type;
    private String measurementName;
    private List<MeasurementDto> measurements;
    private boolean state;
    private String exploitationStartDate;

    public SensorDto(long id, String type, String measurementName, List<MeasurementDto> measurements, boolean state, String exploitationStartDate) {
        this.id = id;
        this.type = type;
        this.measurementName = measurementName;
        this.measurements = measurements;
        this.state = state;
        this.exploitationStartDate = exploitationStartDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public List<MeasurementDto> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementDto> measurements) {
        this.measurements = measurements;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getExploitationStartDate() {
        return exploitationStartDate;
    }

    public void setExploitationStartDate(String exploitationStartDate) {
        this.exploitationStartDate = exploitationStartDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorDto sensorDto = (SensorDto) o;
        return getId() == sensorDto.getId() && isState() == sensorDto.isState() && Objects.equals(getType(), sensorDto.getType()) && Objects.equals(getMeasurementName(), sensorDto.getMeasurementName()) && Objects.equals(getMeasurements(), sensorDto.getMeasurements()) && Objects.equals(getExploitationStartDate(), sensorDto.getExploitationStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getMeasurementName(), getMeasurements(), isState(), getExploitationStartDate());
    }

    @Override
    public String toString() {
        return type;
    }
}
