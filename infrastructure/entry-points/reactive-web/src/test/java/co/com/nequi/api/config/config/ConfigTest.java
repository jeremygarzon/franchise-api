package co.com.nequi.api.config.config;

import co.com.nequi.api.config.CorsConfig;
import co.com.nequi.api.config.SecurityHeadersConfig;
import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.api.handler.FranchiseHandler;
import co.com.nequi.api.router.FranchiseRouter;
import co.com.nequi.model.franchise.gateways.in.FranchiseServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FranchiseRouter.class, FranchiseHandler.class, ApiErrorHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseServicePort franchiseServicePort;


    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(franchiseServicePort.getHighestStockProducts(1L)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/franchises/1/highest-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().doesNotExist("Server")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}