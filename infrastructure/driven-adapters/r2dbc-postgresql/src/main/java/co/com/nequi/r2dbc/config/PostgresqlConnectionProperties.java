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
        Integer maxIdleTimeMinutes,
        String sslMode) {

    public static final int DEFAULT_INITIAL_SIZE = 5;
    public static final int DEFAULT_MAX_SIZE = 20;
    public static final int DEFAULT_MAX_IDLE_TIME_MINUTES = 30;
    // Off by default so local development against a plain PostgreSQL works without TLS.
    // Managed databases such as AWS RDS require TLS, so set this to "require" there.
    public static final String DEFAULT_SSL_MODE = "disable";

    public PostgresqlConnectionProperties {
        initialSize = initialSize == null ? DEFAULT_INITIAL_SIZE : initialSize;
        maxSize = maxSize == null ? DEFAULT_MAX_SIZE : maxSize;
        maxIdleTimeMinutes = maxIdleTimeMinutes == null ? DEFAULT_MAX_IDLE_TIME_MINUTES : maxIdleTimeMinutes;
        sslMode = (sslMode == null || sslMode.isBlank()) ? DEFAULT_SSL_MODE : sslMode;
    }
}
