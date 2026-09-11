package co.com.nequi.r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class R2dbcInitializerConfig {

    @Bean
    public ConnectionFactoryInitializer schemaInitializer(ConnectionFactory connectionFactory) {
        return initializerFor(connectionFactory, "schema.sql");
    }

    @Bean
    @Profile("!prod")
    public ConnectionFactoryInitializer seedInitializer(ConnectionFactory connectionFactory) {
        return initializerFor(connectionFactory, "data.sql");
    }

    private ConnectionFactoryInitializer initializerFor(ConnectionFactory connectionFactory, String script) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(script)));
        return initializer;
    }
}
