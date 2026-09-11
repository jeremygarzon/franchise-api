package co.com.nequi.api.config;

import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.api.handler.FranchiseHandler;
import co.com.nequi.api.router.FranchiseRouter;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.in.FranchiseServicePort;
import co.com.nequi.model.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FranchiseRouter.class, FranchiseHandler.class, ApiErrorHandler.class})
@WebFluxTest
class FranchiseRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseServicePort franchiseServicePort;

    @Test
    void createFranchiseReturns201WithBody() {
        Franchise created = Franchise.builder().id(1L).name("Acme").build();
        when(franchiseServicePort.create(any(Franchise.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Acme\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Acme");
    }

    @Test
    void createFranchiseWithBlankNameReturns400() {
        when(franchiseServicePort.create(any(Franchise.class)))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.FRANCHISE_NAME_REQUIRED)));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("FRANCHISE_NAME_REQUIRED");
    }

    @Test
    void updateFranchiseNameReturns200WithBody() {
        Franchise updated = Franchise.builder().id(5L).name("Acme Global").build();
        when(franchiseServicePort.updateName(eq(5L), eq("Acme Global"))).thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/franchises/5/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Acme Global\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(5)
                .jsonPath("$.name").isEqualTo("Acme Global");
    }

    @Test
    void updateFranchiseNameWhenMissingReturns404() {
        when(franchiseServicePort.updateName(eq(99L), any()))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.FRANCHISE_NOT_FOUND)));

        webTestClient.patch()
                .uri("/api/franchises/99/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Whatever\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("FRANCHISE_NOT_FOUND");
    }

    @Test
    void getHighestStockProductsReturns200WithList() {
        Product top = Product.builder().id(7L).name("Alpha").stock(80).branchId(2L).build();
        when(franchiseServicePort.getHighestStockProducts(1L)).thenReturn(Flux.just(top));

        webTestClient.get()
                .uri("/api/franchises/1/highest-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(7)
                .jsonPath("$[0].name").isEqualTo("Alpha")
                .jsonPath("$[0].stock").isEqualTo(80)
                .jsonPath("$[0].branchId").isEqualTo(2);
    }

    @Test
    void unexpectedErrorReturns500() {
        when(franchiseServicePort.getHighestStockProducts(1L))
                .thenReturn(Flux.error(new RuntimeException("boom")));

        webTestClient.get()
                .uri("/api/franchises/1/highest-stock-products")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
    }
}
