package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.out.BranchRepository;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.out.FranchiseRepository;
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
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    private BranchUseCase branchUseCase;

    @BeforeEach
    void setUp() {
        branchUseCase = new BranchUseCase(branchRepository, franchiseRepository);
    }

    @Test
    void shouldCreateBranchSuccessfully() {
        Long franchiseId = 10L;
        Branch expected = Branch.builder()
                .name("Branch One")
                .franchiseId(franchiseId)
                .build();
        Franchise parent = Franchise.builder().id(franchiseId).name("Franchise One").build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(parent));
        when(branchRepository.save(expected)).thenReturn(Mono.just(expected));

        StepVerifier.create(branchUseCase.create(expected))
                .expectNext(expected)
                .verifyComplete();

        verify(franchiseRepository).findById(franchiseId);
        verify(branchRepository).save(expected);
    }

    @Test
    void shouldRejectNullBranch() {
        StepVerifier.create(branchUseCase.create(null))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_REQUIRED);
                })
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldRejectBlankBranchName() {
        Branch branch = Branch.builder()
                .name("   ")
                .franchiseId(10L)
                .build();

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_NAME_REQUIRED);
                })
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenFranchiseIdIsNull() {
        Branch branch = Branch.builder()
                .name("Branch One")
                .franchiseId(null)
                .build();

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_ID_REQUIRED);
                })
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldRejectCreateWhenParentFranchiseNotFound() {
        Long franchiseId = 99L;
        Branch branch = Branch.builder()
                .name("Branch One")
                .franchiseId(franchiseId)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.FRANCHISE_NOT_FOUND);
                })
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldUpdateBranchNamePreservingIdAndFranchiseId() {
        Long id = 42L;
        Long franchiseId = 7L;
        Branch existing = Branch.builder().id(id).name("Old Name").franchiseId(franchiseId).build();
        Branch updated = Branch.builder().id(id).name("New Name").franchiseId(franchiseId).build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(existing));
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(branchUseCase.updateName(id, "New Name"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(branchRepository).update(captor.capture());
        Branch passed = captor.getValue();
        assertThat(passed.getId()).isEqualTo(id);
        assertThat(passed.getFranchiseId()).isEqualTo(franchiseId);
        assertThat(passed.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldRejectUpdateNameWhenNameIsBlank() {
        StepVerifier.create(branchUseCase.updateName(1L, "   "))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_NAME_REQUIRED);
                })
                .verify();

        verify(branchRepository, never()).update(any());
    }

    @Test
    void shouldRejectUpdateNameWhenBranchNotFound() {
        Long id = 99L;
        when(branchRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateName(id, "New Name"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(((DomainException) error).getErrorCode())
                            .isEqualTo(DomainErrorCode.BRANCH_NOT_FOUND);
                })
                .verify();

        verify(branchRepository, never()).update(any());
    }
}
