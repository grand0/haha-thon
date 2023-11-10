package ru.kpfu.itis.arifulina.db.generator;

import ru.kpfu.itis.arifulina.db.util.DatabaseConnectionUtil;

import java.sql.*;


public class DataGenerator {
    static Connection connection = DatabaseConnectionUtil.getConnection();
    private final static String MOVING_SENSOR_NAME = "Moving Sensor";
    private final static String LEAKAGE_SENSOR_NAME = "Water Leakage Sensor";
    private final static String WINDOW_BLIND_SENSOR_NAME = "Window Blind Sensor";
    private final static String SMOKE_SENSOR_NAME = "Smoke Sensor";
    private final static String LIGHTING_SENSOR_NAME = "Lighting Sensor";
    private final static String DOOR_WINDOW_SENSOR_NAME = "Door And Window Sensor";
    private final static String DETECTED_MEASURE_NAME = "detected";
    private final static String INTENSITY_MEASURE_NAME = "intensity";
    private final static String COLOR_MEASURE_NAME = "color";
    public static final int DELTA_TIME_MILLIS = 10 * 1000;
    public static final int START_VALUE_INTENSITY = 50;
    public static final int START_VALUE_COLOR = 8388608;
    public static final int COLOR_CHANGING_SPEED = 16*16*16;
    public static final int INTENSITY_CHANGING_SPEED = 10;

    private static void initSensors(){
        try {
            String sql = "INSERT INTO sensors(type_id, state, date) values (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setBoolean(2, true);
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            preparedStatement.setLong(1, getSensorTypeId(MOVING_SENSOR_NAME));
            preparedStatement.executeUpdate();

            preparedStatement.setLong(1, getSensorTypeId(LEAKAGE_SENSOR_NAME));
            preparedStatement.executeUpdate();

            preparedStatement.setLong(1, getSensorTypeId(WINDOW_BLIND_SENSOR_NAME));
            preparedStatement.executeUpdate();

            preparedStatement.setLong(1, getSensorTypeId(SMOKE_SENSOR_NAME));
            preparedStatement.executeUpdate();

            preparedStatement.setLong(1, getSensorTypeId(LIGHTING_SENSOR_NAME));
            preparedStatement.executeUpdate();

            preparedStatement.setLong(1, getSensorTypeId(DOOR_WINDOW_SENSOR_NAME));
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            throw new RuntimeException();
        }
    }

    private static void getDataFromSensors(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM sensors;");
            while (resultSet.next()){
                Thread.sleep(1000);
                if (resultSet.getBoolean("state")){
                    long id = resultSet.getLong("id");
                    switch (getSensorType(resultSet.getLong("type_id"))){
                        case MOVING_SENSOR_NAME -> startBooleanSensorDetection(id, DETECTED_MEASURE_NAME, 0.9);
                        case LEAKAGE_SENSOR_NAME, DOOR_WINDOW_SENSOR_NAME, SMOKE_SENSOR_NAME -> startBooleanSensorDetection(id, DETECTED_MEASURE_NAME, 0.1);
                        case WINDOW_BLIND_SENSOR_NAME -> startBooleanSensorDetection(id, DETECTED_MEASURE_NAME, 0.5);
                        case LIGHTING_SENSOR_NAME -> {
                            startIntSensorDetection(id, INTENSITY_MEASURE_NAME, INTENSITY_CHANGING_SPEED, START_VALUE_INTENSITY, 0.5);
                            startIntSensorDetection(id, COLOR_MEASURE_NAME, COLOR_CHANGING_SPEED, START_VALUE_COLOR, 0.5);
                        }
                    }
                }
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        //initSensors(); if you want to init new ones
        getDataFromSensors();
    }

    private static void startIntSensorDetection(long sensorId, String measurementName, int changeSpeed, int startValue, double probability) {
        long nameId = getMeasurementNameId(measurementName);
        int[] bounds = getMeasurementBounds(nameId);
        int min = bounds[0];
        int max = bounds[1];
        Thread thread = new Thread(() -> {
            while (true) {
                int previousValue = startValue;
                try {
                    Thread.sleep(DELTA_TIME_MILLIS);
                    previousValue = Math.random() < probability ? previousValue + (int) (Math.random() * changeSpeed): previousValue - (int) (Math.random() * changeSpeed);
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
    private static void startBooleanSensorDetection(long sensorId, String measurementName, double probability) {
        long nameId = getMeasurementNameId(measurementName);
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(DELTA_TIME_MILLIS);
                    insertData(sensorId, nameId, Math.random() < probability ? 1 : 0, new Timestamp(System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
    }

    private static void insertData(long sensorId, long nameId, int value, Timestamp timestamp) {
        String sql = "INSERT INTO measurement_int_data (sensor_id, measurement_name_id, value, time) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, sensorId);
            statement.setLong(2, nameId);
            statement.setInt(3, value);
            statement.setTimestamp(4, timestamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] getMeasurementBounds(long measurementId){
        try {
            String sql = "SELECT * FROM measurement_int_constraints WHERE measurement_name_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, measurementId);
            ResultSet resultSet = preparedStatement.executeQuery();
            int[] bounds = new int[2];
            if (resultSet.next()){
                bounds[0] = resultSet.getInt("min");
                bounds[1] = resultSet.getInt("max");
            }
            return bounds;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    private static long getMeasurementNameId(String name){
        try {
            String sql = "SELECT * FROM measurement_names WHERE name=?";
            return getId(name, sql);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    private static long getSensorTypeId(String sensorType){
        try {
            String sql = "SELECT * FROM types WHERE type=?";
            return getId(sensorType, sql);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    private static String getSensorType(long sensorTypeId){
        try {
            String sql = "SELECT * FROM types WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, sensorTypeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String type = null;
            if (resultSet.next()) {
                type = resultSet.getString("type");
            }
            return type;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    private static long getId(String name, String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;
    }
}

