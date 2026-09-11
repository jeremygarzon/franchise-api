package co.com.nequi.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostgreSQLConnectionPoolTest {

    private final PostgreSQLConnectionPool connectionPool = new PostgreSQLConnectionPool();

    @Test
    void buildsPoolWithExplicitSizing() {
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
                "localhost", 5432, "dbName", "public", "username", "password",
                5, 20, 30);

        ConnectionPool pool = connectionPool.getConnectionConfig(properties);

        assertNotNull(pool);
    }

    @Test
    void buildsPoolWithDefaultSizingWhenNotProvided() {
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
                "localhost", 5432, "dbName", "public", "username", "password",
                null, null, null);

        ConnectionPool pool = connectionPool.getConnectionConfig(properties);

        assertNotNull(pool);
    }
}
