package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.out.BranchRepository;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        productUseCase = new ProductUseCase(productRepository, branchRepository);
    }


    @Test
    void shouldCreateProductSuccessfully() {
        Long branchId = 10L;
        Product expected = Product.builder()
                .name("Product One")
                .stock(5)
                .branchId(branchId)
                .build();
        Branch parent = Branch.builder().id(branchId).name("Branch One").franchiseId(1L).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(parent));
        when(productRepository.save(expected)).thenReturn(Mono.just(expected));

        StepVerifier.create(productUseCase.create(expected))
                .expectNext(expected)
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(productRepository).save(expected);
    }

    @Test
    void shouldRejectNullProduct() {
        StepVerifier.create(productUseCase.create(null))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_REQUIRED);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectBlankProductName() {
        Product product = Product.builder()
                .name("   ")
                .stock(5)
                .branchId(10L)
                .build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_NAME_REQUIRED);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenStockIsNegative() {
        Product product = Product.builder()
                .name("Product One")
                .stock(-1)
                .branchId(10L)
                .build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_STOCK_INVALID);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenStockIsNull() {
        Product product = Product.builder()
                .name("Product One")
                .stock(null)
                .branchId(10L)
                .build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_STOCK_INVALID);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenBranchIdIsNull() {
        Product product = Product.builder()
                .name("Product One")
                .stock(5)
                .branchId(null)
                .build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_ID_REQUIRED);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenParentBranchNotFound() {
        Long branchId = 99L;
        Product product = Product.builder()
                .name("Product One")
                .stock(5)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_NOT_FOUND);
                })
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingProduct() {
        Long id = 42L;
        Product existing = Product.builder().id(id).name("Product One").stock(5).branchId(10L).build();

        when(productRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete(id))
                .verifyComplete();

        verify(productRepository).deleteById(id);
    }

    @Test
    void shouldRejectDeleteWhenProductNotFound() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete(id))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_NOT_FOUND);
                })
                .verify();

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void shouldUpdateStockPreservingIdNameAndBranchId() {
        Long id = 42L;
        Long branchId = 7L;
        Product existing = Product.builder().id(id).name("Product One").stock(5).branchId(branchId).build();
        Product updated = Product.builder().id(id).name("Product One").stock(20).branchId(branchId).build();

        when(productRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.updateStock(id, 20))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).update(captor.capture());
        Product passed = captor.getValue();
        assertThat(passed.getId()).isEqualTo(id);
        assertThat(passed.getName()).isEqualTo("Product One");
        assertThat(passed.getBranchId()).isEqualTo(branchId);
        assertThat(passed.getStock()).isEqualTo(20);
    }

    @Test
    void shouldRejectUpdateStockWhenNegative() {
        StepVerifier.create(productUseCase.updateStock(1L, -1))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_STOCK_INVALID);
                })
                .verify();

        verify(productRepository, never()).update(any());
    }

    @Test
    void shouldRejectUpdateStockWhenProductNotFound() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(id, 20))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_NOT_FOUND);
                })
                .verify();

        verify(productRepository, never()).update(any());
    }

    @Test
    void shouldUpdateNamePreservingIdStockAndBranchId() {
        Long id = 42L;
        Long branchId = 7L;
        Product existing = Product.builder().id(id).name("Old Name").stock(5).branchId(branchId).build();
        Product updated = Product.builder().id(id).name("New Name").stock(5).branchId(branchId).build();

        when(productRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.updateName(id, "New Name"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).update(captor.capture());
        Product passed = captor.getValue();
        assertThat(passed.getId()).isEqualTo(id);
        assertThat(passed.getStock()).isEqualTo(5);
        assertThat(passed.getBranchId()).isEqualTo(branchId);
        assertThat(passed.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldRejectUpdateNameWhenNameIsBlank() {
        StepVerifier.create(productUseCase.updateName(1L, "   "))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_NAME_REQUIRED);
                })
                .verify();

        verify(productRepository, never()).update(any());
    }

    @Test
    void shouldRejectUpdateNameWhenProductNotFound() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateName(id, "New Name"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.PRODUCT_NOT_FOUND);
                })
                .verify();

        verify(productRepository, never()).update(any());
    }
}
