package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.in.BranchServicePort;
import co.com.nequi.model.branch.gateways.out.BranchRepository;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.franchise.gateways.out.FranchiseRepository;
import co.com.nequi.usecase.util.DomainValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase implements BranchServicePort {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    @Override
    public Mono<Branch> create(Branch branch) {
        return DomainValidations.required(branch, DomainErrorCode.BRANCH_REQUIRED)
                .flatMap(value ->
                        DomainValidations.notBlank(value.getName(), DomainErrorCode.BRANCH_NAME_REQUIRED)
                                .then(DomainValidations.required(value.getFranchiseId(),DomainErrorCode.FRANCHISE_ID_REQUIRED))
                                .flatMap(franchiseRepository::findById)
                                .switchIfEmpty(Mono.error(new DomainException(DomainErrorCode.FRANCHISE_NOT_FOUND)))
                                .thenReturn(value))
                .flatMap(branchRepository::save);
    }

    @Override
    public Mono<Branch> updateName(Long branchId, String name) {
        return DomainValidations.notBlank(name, DomainErrorCode.BRANCH_NAME_REQUIRED)
                .then(existingBranch(branchId))
                .map(b -> Branch.builder()
                        .id(b.getId())
                        .name(name)
                        .franchiseId(b.getFranchiseId())
                        .build())
                .flatMap(branchRepository::update);
    }

    private Mono<Branch> existingBranch(Long id) {
        return DomainValidations.required(id, DomainErrorCode.BRANCH_ID_REQUIRED)
                .flatMap(branchRepository::findById)
                .switchIfEmpty(Mono.error(new DomainException(DomainErrorCode.BRANCH_NOT_FOUND)));
    }
}
