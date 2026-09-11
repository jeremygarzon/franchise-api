package co.com.nequi.api.config;

import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.api.handler.BranchHandler;
import co.com.nequi.api.router.BranchRouter;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.in.BranchServicePort;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
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

@ContextConfiguration(classes = {BranchRouter.class, BranchHandler.class, ApiErrorHandler.class})
@WebFluxTest
class BranchRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BranchServicePort branchServicePort;

    @Test
    void createBranchReturns200WithBody() {
        Branch created = Branch.builder().id(2L).name("Downtown").franchiseId(1L).build();
        when(branchServicePort.create(any(Branch.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Downtown\",\"franchiseId\":1}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(2)
                .jsonPath("$.name").isEqualTo("Downtown")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void createBranchWhenParentFranchiseMissingReturns404() {
        when(branchServicePort.create(any(Branch.class)))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.FRANCHISE_NOT_FOUND)));

        webTestClient.post()
                .uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Downtown\",\"franchiseId\":99}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("FRANCHISE_NOT_FOUND");
    }

    @Test
    void updateBranchNameReturns200WithBody() {
        Branch updated = Branch.builder().id(2L).name("Center").franchiseId(1L).build();
        when(branchServicePort.updateName(eq(2L), eq("Center"))).thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/branches/2/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Center\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Center");
    }

    @Test
    void updateBranchNameWhenMissingReturns404() {
        when(branchServicePort.updateName(eq(99L), any()))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.BRANCH_NOT_FOUND)));

        webTestClient.patch()
                .uri("/api/branches/99/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Center\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BRANCH_NOT_FOUND");
    }

    @Test
    void updateBranchNameWhenBlankReturns400() {
        when(branchServicePort.updateName(eq(2L), any()))
                .thenReturn(Mono.error(new DomainException(DomainErrorCode.BRANCH_NAME_REQUIRED)));

        webTestClient.patch()
                .uri("/api/branches/2/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"  \"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BRANCH_NAME_REQUIRED");
    }
}
