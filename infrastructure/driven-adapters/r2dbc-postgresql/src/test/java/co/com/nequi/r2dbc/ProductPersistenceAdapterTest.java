package co.com.nequi.r2dbc;

import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.entity.ProductEntity;
import co.com.nequi.r2dbc.repository.ProductDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private ProductDataRepository repository;

    @Mock
    private ResilienceDecorator resilience;

    @Captor
    private ArgumentCaptor<ProductEntity> entityCaptor;

    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductPersistenceAdapter(repository, resilience);
    }

    private void passThroughMono() {
        when(resilience.<Product>decorate(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void saveMapsDomainToEntityKeepingBranchAssociation() {
        passThroughMono();
        Product toSave = Product.builder().name("Widget").stock(25).branchId(2L).build();
        when(repository.save(any(ProductEntity.class)))
                .thenReturn(Mono.just(ProductEntity.builder().id(30L).name("Widget").stock(25).branchId(2L).build()));

        StepVerifier.create(adapter.save(toSave))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(30L);
                    assertThat(saved.getStock()).isEqualTo(25);
                    assertThat(saved.getBranchId()).isEqualTo(2L);
                })
                .verifyComplete();

        Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue().getName()).isEqualTo("Widget");
    }

    @Test
    void findByIdReturnsMappedDomainWhenProductExists() {
        passThroughMono();
        when(repository.findById(4L))
                .thenReturn(Mono.just(ProductEntity.builder().id(4L).name("Gadget").stock(10).branchId(2L).build()));

        StepVerifier.create(adapter.findById(4L))
                .assertNext(found -> {
                    assertThat(found.getName()).isEqualTo("Gadget");
                    assertThat(found.getStock()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    void findByIdCompletesEmptyWhenProductDoesNotExist() {
        passThroughMono();
        when(repository.findById(404L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(404L))
                .verifyComplete();
    }

    @Test
    void updatePersistsProductWithExistingId() {
        passThroughMono();
        Product toUpdate = Product.builder().id(5L).name("Gadget").stock(3).branchId(2L).build();
        when(repository.save(any(ProductEntity.class)))
                .thenReturn(Mono.just(ProductEntity.builder().id(5L).name("Gadget").stock(3).branchId(2L).build()));

        StepVerifier.create(adapter.update(toUpdate))
                .assertNext(updated -> assertThat(updated.getStock()).isEqualTo(3))
                .verifyComplete();

        Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo(5L);
    }

    @Test
    void deleteByIdRemovesProductAndCompletes() {
        when(resilience.<Void>decorate(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.deleteById(9L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(9L))
                .verifyComplete();

        Mockito.verify(repository).deleteById(9L);
    }

    @Test
    void findHighestStockProductsByFranchiseIdMapsEachEntityToDomain() {
        when(resilience.<Product>decorate(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findHighestStockProductsByFranchiseId(100L))
                .thenReturn(Flux.just(
                        ProductEntity.builder().id(1L).name("Top A").stock(50).branchId(2L).build(),
                        ProductEntity.builder().id(2L).name("Top B").stock(40).branchId(3L).build()));

        StepVerifier.create(adapter.findHighestStockProductsByFranchiseId(100L))
                .assertNext(product -> {
                    assertThat(product.getName()).isEqualTo("Top A");
                    assertThat(product.getStock()).isEqualTo(50);
                    assertThat(product.getBranchId()).isEqualTo(2L);
                })
                .assertNext(product -> {
                    assertThat(product.getName()).isEqualTo("Top B");
                    assertThat(product.getStock()).isEqualTo(40);
                    assertThat(product.getBranchId()).isEqualTo(3L);
                })
                .verifyComplete();
    }

    @Test
    void findHighestStockProductsReturnsEmptyWhenFranchiseHasNoProducts() {
        when(resilience.<Product>decorate(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findHighestStockProductsByFranchiseId(200L)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findHighestStockProductsByFranchiseId(200L))
                .verifyComplete();
    }
}
