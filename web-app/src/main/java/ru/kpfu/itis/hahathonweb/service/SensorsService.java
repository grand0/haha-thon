package ru.kpfu.itis.hahathonweb.service;

import ru.kpfu.itis.hahathonweb.dao.MeasurementIntDataDao;
import ru.kpfu.itis.hahathonweb.dao.MeasurementNamesDao;
import ru.kpfu.itis.hahathonweb.dao.SensorDao;
import ru.kpfu.itis.hahathonweb.dao.TypeDao;
import ru.kpfu.itis.hahathonweb.dto.MeasurementDto;
import ru.kpfu.itis.hahathonweb.dto.SensorDto;
import ru.kpfu.itis.hahathonweb.model.MeasurementIntData;
import ru.kpfu.itis.hahathonweb.model.Sensor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SensorsService {
    private final MeasurementIntDataDao measurementIntDataDao;
    private final MeasurementNamesDao measurementNamesDao;
    private final SensorDao sensorDao;
    private final TypeDao typeDao;

    public SensorsService(MeasurementIntDataDao measurementIntDataDao, MeasurementNamesDao measurementNamesDao, SensorDao sensorDao, TypeDao typeDao) {
        this.measurementIntDataDao = measurementIntDataDao;
        this.measurementNamesDao = measurementNamesDao;
        this.sensorDao = sensorDao;
        this.typeDao = typeDao;
    }

    public List<SensorDto> getAll() {
        List<MeasurementIntData> measurementIntData = measurementIntDataDao.getAll();
        return measurementIntData.stream()
                .map(measurement -> new SensorMeasurement(measurement.getSensorId(), measurement.getMeasure()))
                .distinct()
                .map(sensorMeasurement -> {
                    List<MeasurementDto> measurements = measurementIntData.stream()
                            .filter(measurement -> new SensorMeasurement(measurement.getSensorId(), measurement.getMeasure()).equals(sensorMeasurement))
                            .map(measurement -> new MeasurementDto(measurement.getValue(), measurement.getTime()))
                            .collect(Collectors.toList());
                    Sensor sensor = sensorDao.getById(sensorMeasurement.getSensorId());
                    String measurementName = measurementNamesDao.getById(sensorMeasurement.getMeasurementNameId());
                    return new SensorDto(
                            sensorMeasurement.getSensorId(),
                            typeDao.getById(sensor.getTypeId()),
                            measurementName,
                            measurements,
                            sensor.getState(),
                            sensor.getDate().toLocalDateTime().toString()
                    );
                })
                .collect(Collectors.toList());
    }

    private static class SensorMeasurement {
        private long sensorId;
        private long measurementNameId;

        public SensorMeasurement(long sensorId, long measurementNameId) {
            this.sensorId = sensorId;
            this.measurementNameId = measurementNameId;
        }

        public long getSensorId() {
            return sensorId;
        }

        public void setSensorId(long sensorId) {
            this.sensorId = sensorId;
        }

        public long getMeasurementNameId() {
            return measurementNameId;
        }

        public void setMeasurementNameId(long measurementNameId) {
            this.measurementNameId = measurementNameId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SensorMeasurement that = (SensorMeasurement) o;
            return getSensorId() == that.getSensorId() && getMeasurementNameId() == that.getMeasurementNameId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSensorId(), getMeasurementNameId());
        }
    }
}
