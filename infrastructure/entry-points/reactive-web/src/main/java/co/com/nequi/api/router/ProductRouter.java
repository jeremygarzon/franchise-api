package co.com.nequi.api.router;

import co.com.nequi.api.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route(POST("/api/products"), handler::createProduct)
                .andRoute(DELETE("/api/products/{productId}"), handler::deleteProduct)
                .andRoute(PATCH("/api/products/{productId}/stock"), handler::updateProductStock)
                .andRoute(PATCH("/api/products/{productId}/name"), handler::updateProductName);
    }
}
