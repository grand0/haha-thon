package ru.kpfu.itis.hahathonweb.dao;

import ru.kpfu.itis.hahathonweb.model.MeasurementIntData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.kpfu.itis.hahathonweb.util.DatabaseConnectionUtil.getConnection;

public class MeasurementIntDataDao {
    Connection connection = getConnection();

    public List<MeasurementIntData> getAll() {
        List<MeasurementIntData> measurementIntData = new ArrayList<>();
        String sql = "SELECT * FROM measurement_int_data ORDER BY sensor_id, measurement_name_id, \"time\";";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                measurementIntData.add(resultSetToSensor(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return measurementIntData;
    }

    public MeasurementIntData getById(long id) {
        String sql = "SELECT * FROM measurement_int_data WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSetToSensor(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private MeasurementIntData resultSetToSensor(ResultSet resultSet) throws SQLException {
        return new MeasurementIntData(
                resultSet.getLong("sensor_id"),
                resultSet.getLong("measurement_name_id"),
                resultSet.getInt("value"),
                resultSet.getTimestamp("time")
        );
    }
}

