package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.out.FranchiseRepository;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ProductRepository productRepository;

    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseUseCase = new FranchiseUseCase(franchiseRepository, productRepository);
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        Franchise expected = Franchise.builder()
                .name("Franchise One")
                .build();

        when(franchiseRepository.save(expected)).thenReturn(Mono.just(expected));

        StepVerifier.create(franchiseUseCase.create(expected))
                .expectNext(expected)
                .verifyComplete();

        verify(franchiseRepository).save(expected);
    }

    @Test
    void shouldRejectNullFranchise() {
        StepVerifier.create(franchiseUseCase.create(null))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_REQUIRED);
                })
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void shouldRejectEmptyFranchiseName() {
        Franchise franchise = Franchise.builder()
                .name("   ")
                .build();

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_NAME_REQUIRED);
                })
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateFranchiseNamePreservingId() {
        Long id = 42L;
        Franchise existing = Franchise.builder().id(id).name("Old Name").build();
        Franchise updated = Franchise.builder().id(id).name("New Name").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseUseCase.updateName(id, "New Name"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).update(captor.capture());
        Franchise passed = captor.getValue();
        assertThat(passed.getId()).isEqualTo(id);
        assertThat(passed.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldRejectUpdateNameWhenNameIsBlank() {
        StepVerifier.create(franchiseUseCase.updateName(1L, "   "))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_NAME_REQUIRED);
                })
                .verify();

        verify(franchiseRepository, never()).update(any());
    }

    @Test
    void shouldRejectUpdateNameWhenIdIsNull() {
        StepVerifier.create(franchiseUseCase.updateName(null, "New Name"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_ID_REQUIRED);
                })
                .verify();

        verify(franchiseRepository, never()).update(any());
    }

    @Test
    void shouldRejectUpdateNameWhenFranchiseNotFound() {
        Long id = 99L;
        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateName(id, "New Name"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_NOT_FOUND);
                })
                .verify();

        verify(franchiseRepository, never()).update(any());
    }

    @Test
    void shouldReturnHighestStockProductsPerBranch() {
        Long id = 7L;
        Franchise existing = Franchise.builder().id(id).name("Franchise Seven").build();
        Product topBranch1 = Product.builder().id(1L).name("Alpha").stock(50).branchId(100L).build();
        Product topBranch2 = Product.builder().id(2L).name("Beta").stock(80).branchId(200L).build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.findHighestStockProductsByFranchiseId(id))
                .thenReturn(Flux.just(topBranch1, topBranch2));

        StepVerifier.create(franchiseUseCase.getHighestStockProducts(id))
                .expectNext(topBranch1, topBranch2)
                .verifyComplete();

        verify(productRepository).findHighestStockProductsByFranchiseId(id);
    }

    @Test
    void shouldRejectGetHighestStockProductsWhenIdIsNull() {
        StepVerifier.create(franchiseUseCase.getHighestStockProducts(null))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_ID_REQUIRED);
                })
                .verify();

        verify(productRepository, never()).findHighestStockProductsByFranchiseId(any());
    }

    @Test
    void shouldRejectGetHighestStockProductsWhenFranchiseNotFound() {
        Long id = 123L;
        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getHighestStockProducts(id))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_NOT_FOUND);
                })
                .verify();

        verify(productRepository, never()).findHighestStockProductsByFranchiseId(any());
    }

    @Test
    void shouldReturnEmptyWhenNoBranchesHaveProducts() {
        Long id = 5L;
        Franchise existing = Franchise.builder().id(id).name("Franchise Five").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.findHighestStockProductsByFranchiseId(id))
                .thenReturn(Flux.fromIterable(List.of()));

        StepVerifier.create(franchiseUseCase.getHighestStockProducts(id))
                .verifyComplete();

        verify(productRepository).findHighestStockProductsByFranchiseId(id);
    }
}
