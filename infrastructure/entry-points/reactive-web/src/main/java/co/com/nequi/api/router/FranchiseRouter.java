package co.com.nequi.api.router;

import co.com.nequi.api.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(GET("/api/franchises/{franchiseId}/highest-stock-products"),
                        handler::getHighestStockProducts)
                .andRoute(PATCH("/api/franchises/{franchiseId}/name"),
                        handler::updateFranchiseName);
    }
}
