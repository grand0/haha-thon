package ru.kpfu.itis.hahathonweb.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Sensor {
    private long id;
    private long typeId;
    private boolean state;
    private Timestamp date;

    public Sensor(long id, long typeId, boolean state, Timestamp date) {
        this.id = id;
        this.typeId = typeId;
        this.state = state;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return getId() == sensor.getId() && getTypeId() == sensor.getTypeId() && getState() == sensor.getState() && Objects.equals(getDate(), sensor.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTypeId(), getState(), getDate());
    }
}
