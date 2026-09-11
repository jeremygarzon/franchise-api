package co.com.nequi.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.r2dbc")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password,
        Integer initialSize,
        Integer maxSize,
        Integer maxIdleTimeMinutes) {

    public static final int DEFAULT_INITIAL_SIZE = 5;
    public static final int DEFAULT_MAX_SIZE = 20;
    public static final int DEFAULT_MAX_IDLE_TIME_MINUTES = 30;

    public PostgresqlConnectionProperties {
        initialSize = initialSize == null ? DEFAULT_INITIAL_SIZE : initialSize;
        maxSize = maxSize == null ? DEFAULT_MAX_SIZE : maxSize;
        maxIdleTimeMinutes = maxIdleTimeMinutes == null ? DEFAULT_MAX_IDLE_TIME_MINUTES : maxIdleTimeMinutes;
    }
}
