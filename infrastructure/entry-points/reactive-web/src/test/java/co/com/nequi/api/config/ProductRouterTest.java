package co.com.nequi.api.config;

import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.api.handler.ProductHandler;
import co.com.nequi.api.router.ProductRouter;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.in.ProductServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ProductRouter.class, ProductHandler.class, ApiErrorHandler.class})
@WebFluxTest
class ProductRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductServicePort productServicePort;

    @Test
    void createProductReturns200WithBody() {
        Product created = Product.builder().id(3L).name("Widget").stock(25).branchId(2L).build();
        when(productServicePort.create(any(Product.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Widget\",\"stock\":25,\"branchId\":2}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.name").isEqualTo("Widget")
                .jsonPath("$.stock").isEqualTo(25)
                .jsonPath("$.branchId").isEqualTo(2);
    }

    @Test
    void createProductWithNegativeStockReturns400() {
        when(productServicePort.create(any(Product.class)))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.PRODUCT_STOCK_INVALID)));

        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Widget\",\"stock\":-1,\"branchId\":2}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("PRODUCT_STOCK_INVALID");
    }

    @Test
    void deleteProductReturns200WithEmptyBody() {
        when(productServicePort.delete(3L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void deleteMissingProductReturns404() {
        when(productServicePort.delete(99L))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.PRODUCT_NOT_FOUND)));

        webTestClient.delete()
                .uri("/api/products/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("PRODUCT_NOT_FOUND");
    }

    @Test
    void updateProductStockReturns200WithBody() {
        Product updated = Product.builder().id(3L).name("Widget").stock(50).branchId(2L).build();
        when(productServicePort.updateStock(eq(3L), eq(50))).thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/products/3/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":50}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    void updateProductNameReturns200WithBody() {
        Product updated = Product.builder().id(3L).name("Gadget").stock(25).branchId(2L).build();
        when(productServicePort.updateName(eq(3L), eq("Gadget"))).thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/products/3/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Gadget\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Gadget");
    }

    @Test
    void unexpectedErrorReturns500() {
        when(productServicePort.updateName(eq(3L), any()))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        webTestClient.patch()
                .uri("/api/products/3/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Gadget\"}")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
    }
}
