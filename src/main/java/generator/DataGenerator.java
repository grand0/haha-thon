package generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static generator.DatabaseConnectionUtil.getConnection;


public class DataGenerator {
    static Connection connection = getConnection();
    private static final long MOVING_SENSOR_ID = 1;
    private static final long LEAKAGE_SENSOR_ID = 2;
    private static final long WINDOW_BLIND_SENSOR_ID = 3;
    private static final long SMOKE_SENSOR_ID = 4;
    private static final long LIGHTING_SENSOR_ID = 5;
    private static final long OPENING_SENSOR_ID = 6;

    private static final long COLOR_NAME_ID = 1;
    private static final long INTENSITY_NAME_ID = 2;
    private static final long DETECTED_NAME_ID = 3;


    public static void main(String[] args) {
        try {
            Thread.sleep(1000);
            startBooleanSensorDetection(MOVING_SENSOR_ID, DETECTED_NAME_ID, 0.9);
            Thread.sleep(1000);
            startBooleanSensorDetection(LEAKAGE_SENSOR_ID, DETECTED_NAME_ID, 0.1);
            Thread.sleep(1000);
            startBooleanSensorDetection(WINDOW_BLIND_SENSOR_ID, DETECTED_NAME_ID, 0.5);
            Thread.sleep(1000);
            startBooleanSensorDetection(SMOKE_SENSOR_ID, DETECTED_NAME_ID, 0.1);
            Thread.sleep(1000);
            startBooleanSensorDetection(OPENING_SENSOR_ID, DETECTED_NAME_ID, 0.1);
            Thread.sleep(1000);
            startLightingSensorDetection(LIGHTING_SENSOR_ID, INTENSITY_NAME_ID, 10, 0, 100);
            startLightingSensorDetection(LIGHTING_SENSOR_ID, COLOR_NAME_ID, 16*16*16, 0, (int) (Math.pow(16, 6) - 1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startLightingSensorDetection(long sensorId, long nameId, int changeSpeed, int min, int max) {
        Thread thread = new Thread(() -> {
            while (true) {
                int previousValue = 0;
                try {
                    Thread.sleep(10 * 1000);
                    previousValue = Math.random() < 0.5 ? previousValue + (int) (Math.random() * changeSpeed): previousValue - (int) (Math.random() * changeSpeed);
                    if (previousValue < min) previousValue = min;
                    if (previousValue > max) previousValue = max;
                    insertData(sensorId, nameId, previousValue, new Timestamp(System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
    }
    private static void startBooleanSensorDetection(long id, long nameId, double probability) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10 * 1000);
                    insertData(id, nameId, Math.random() < probability ? 1 : 0, new Timestamp(System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
    }

    private static void insertData(long sensorId, long nameId, int value, Timestamp timestamp) {
        String sql = "INSERT INTO measurement_int_data (id, sensor_id, measurement_name_id, value, time) VALUES (?, ?, ?, ?, ?)";


        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, 1L);
            statement.setLong(2, sensorId);
            statement.setLong(3, nameId);
            statement.setInt(4, value);
            statement.setTimestamp(5, timestamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
