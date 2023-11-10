package ru.kpfu.itis.arifulina.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtil {
    private static Connection connection;
    private static final String URL = "jdbc:postgresql://localhost:5432/hahathon";
    private static final String USER = "your user";
    private static final String PASSWORD = "your password";

    public static Connection getConnection()  {
        if (connection == null) {

            try {
                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection(
                        URL,
                        USER,
                        PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }
}

