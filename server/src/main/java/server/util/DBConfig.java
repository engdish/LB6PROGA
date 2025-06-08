package server.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {
    private static final String PROPS_FILE = "application.properties";
    private static HikariDataSource dataSource;

    static {
        try (var in = DBConfig.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            var props = new Properties();
            props.load(in);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));

            config.setMaximumPoolSize(
                    Integer.parseInt(props.getProperty("db.pool.maximumPoolSize")));
            config.setMinimumIdle(
                    Integer.parseInt(props.getProperty("db.pool.minimumIdle")));
            config.setConnectionTimeout(
                    Long.parseLong(props.getProperty("db.pool.connectionTimeout")));
            config.setIdleTimeout(
                    Long.parseLong(props.getProperty("db.pool.idleTimeout")));
            config.setMaxLifetime(
                    Long.parseLong(props.getProperty("db.pool.maxLifetime")));

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Не удалось инициализировать пул соединений: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdownPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
