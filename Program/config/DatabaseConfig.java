package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import helpers.EnvReader;

public class DatabaseConfig {
    private static final String URL = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;", EnvReader.readEnv("DB_HOST"), EnvReader.readEnv("DB_PORT"), EnvReader.readEnv("DB_NAME"));
    private static final String USER = EnvReader.readEnv("DB_USER");
    private static final String PASSWORD = EnvReader.readEnv("DB_PASSWORD");
    
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC Driver not found: " + e.getMessage());
                throw new SQLException(e);
            }
        }
        return connection;
    }
}
