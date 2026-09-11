package co.com.nequi.r2dbc;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.entity.FranchiseEntity;
import co.com.nequi.r2dbc.repository.FranchiseDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchisePersistenceAdapterTest {

    @Mock
    private FranchiseDataRepository repository;

    @Mock
    private ResilienceDecorator resilience;

    @Captor
    private ArgumentCaptor<FranchiseEntity> entityCaptor;

    private FranchisePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchisePersistenceAdapter(repository, resilience);
        when(resilience.<Franchise>decorate(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void savePersistsDomainAsEntityAndReturnsPersistedDomain() {
        Franchise toSave = Franchise.builder().name("Acme").build();
        FranchiseEntity persisted = FranchiseEntity.builder().id(10L).name("Acme").build();
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(adapter.save(toSave))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(10L);
                    assertThat(saved.getName()).isEqualTo("Acme");
                })
                .verifyComplete();

        org.mockito.Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue().getName()).isEqualTo("Acme");
    }

    @Test
    void findByIdReturnsMappedDomainWhenFranchiseExists() {
        when(repository.findById(5L))
                .thenReturn(Mono.just(FranchiseEntity.builder().id(5L).name("Globex").build()));

        StepVerifier.create(adapter.findById(5L))
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(5L);
                    assertThat(found.getName()).isEqualTo("Globex");
                })
                .verifyComplete();
    }

    @Test
    void findByIdCompletesEmptyWhenFranchiseDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(404L))
                .verifyComplete();
    }

    @Test
    void updatePersistsFranchiseWithExistingId() {
        Franchise toUpdate = Franchise.builder().id(7L).name("Renamed").build();
        when(repository.save(any(FranchiseEntity.class)))
                .thenReturn(Mono.just(FranchiseEntity.builder().id(7L).name("Renamed").build()));

        StepVerifier.create(adapter.update(toUpdate))
                .assertNext(updated -> {
                    assertThat(updated.getId()).isEqualTo(7L);
                    assertThat(updated.getName()).isEqualTo("Renamed");
                })
                .verifyComplete();

        org.mockito.Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo(7L);
    }

    @Test
    void savePropagatesRepositoryError() {
        when(repository.save(any(FranchiseEntity.class)))
                .thenReturn(Mono.error(new IllegalStateException("db down")));

        StepVerifier.create(adapter.save(Franchise.builder().name("Acme").build()))
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && "db down".equals(error.getMessage()))
                .verify();
    }
}
