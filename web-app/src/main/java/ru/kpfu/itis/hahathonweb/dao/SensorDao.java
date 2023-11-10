package ru.kpfu.itis.hahathonweb.dao;

import ru.kpfu.itis.hahathonweb.model.Sensor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.kpfu.itis.hahathonweb.util.DatabaseConnectionUtil.getConnection;

public class SensorDao {
    Connection connection = getConnection();

    public List<Sensor> getAll() {
        List<Sensor> sensors = new ArrayList<>();
        String sql = "SELECT * FROM sensors;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                sensors.add(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sensors;
    }

    public Sensor getById(long id) {
        String sql = "SELECT * FROM sensors WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return fromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void toggle(long id) {
        String sql = "UPDATE sensors SET state = NOT state WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Sensor fromResultSet(ResultSet set) throws SQLException {
        return new Sensor(
                set.getLong("id"),
                set.getLong("type_id"),
                set.getBoolean("state"),
                set.getTimestamp("date")
        );
    }
}
