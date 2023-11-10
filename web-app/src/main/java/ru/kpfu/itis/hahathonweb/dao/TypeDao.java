package ru.kpfu.itis.hahathonweb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.kpfu.itis.hahathonweb.util.DatabaseConnectionUtil.getConnection;

public class TypeDao {
    Connection connection = getConnection();

    public String getById(long id) {
        String sql = "SELECT type FROM types WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("type");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
