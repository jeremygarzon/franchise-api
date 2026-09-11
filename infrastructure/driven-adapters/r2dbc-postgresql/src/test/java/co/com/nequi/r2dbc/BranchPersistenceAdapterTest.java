package co.com.nequi.r2dbc;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.r2dbc.entity.BranchEntity;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.repository.BranchDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchPersistenceAdapterTest {

    @Mock
    private BranchDataRepository repository;

    @Mock
    private ResilienceDecorator resilience;

    @Captor
    private ArgumentCaptor<BranchEntity> entityCaptor;

    private BranchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchPersistenceAdapter(repository, resilience);
        when(resilience.<Branch>decorate(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void saveMapsDomainToEntityKeepingFranchiseAssociation() {
        Branch toSave = Branch.builder().name("Downtown").franchiseId(1L).build();
        BranchEntity persisted = BranchEntity.builder().id(20L).name("Downtown").franchiseId(1L).build();
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(adapter.save(toSave))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(20L);
                    assertThat(saved.getName()).isEqualTo("Downtown");
                    assertThat(saved.getFranchiseId()).isEqualTo(1L);
                })
                .verifyComplete();

        Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue().getFranchiseId()).isEqualTo(1L);
    }

    @Test
    void findByIdReturnsMappedDomainWhenBranchExists() {
        when(repository.findById(2L))
                .thenReturn(Mono.just(BranchEntity.builder().id(2L).name("Uptown").franchiseId(1L).build()));

        StepVerifier.create(adapter.findById(2L))
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(2L);
                    assertThat(found.getName()).isEqualTo("Uptown");
                    assertThat(found.getFranchiseId()).isEqualTo(1L);
                })
                .verifyComplete();
    }

    @Test
    void findByIdCompletesEmptyWhenBranchDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(999L))
                .verifyComplete();
    }

    @Test
    void updatePersistsBranchWithExistingId() {
        Branch toUpdate = Branch.builder().id(3L).name("Renamed Branch").franchiseId(1L).build();
        when(repository.save(any(BranchEntity.class)))
                .thenReturn(Mono.just(BranchEntity.builder().id(3L).name("Renamed Branch").franchiseId(1L).build()));

        StepVerifier.create(adapter.update(toUpdate))
                .assertNext(updated -> assertThat(updated.getName()).isEqualTo("Renamed Branch"))
                .verifyComplete();

        Mockito.verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo(3L);
    }

    @Test
    void findByIdPropagatesRepositoryError() {
        when(repository.findById(1L))
                .thenReturn(Mono.error(new IllegalStateException("connection lost")));

        StepVerifier.create(adapter.findById(1L))
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && "connection lost".equals(error.getMessage()))
                .verify();
    }
}
