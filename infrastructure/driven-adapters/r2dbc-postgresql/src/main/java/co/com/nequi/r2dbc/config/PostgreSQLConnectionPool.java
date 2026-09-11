package co.com.nequi.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PostgreSQLConnectionPool {

    @Bean
    public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties) {
        PostgresqlConnectionConfiguration dbConfiguration = PostgresqlConnectionConfiguration.builder()
                .host(properties.host())
                .port(properties.port())
                .database(properties.database())
                .schema(properties.schema())
                .username(properties.username())
                .password(properties.password())
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                .name("api-postgres-connection-pool")
                .initialSize(properties.initialSize())
                .maxSize(properties.maxSize())
                .maxIdleTime(Duration.ofMinutes(properties.maxIdleTimeMinutes()))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}
