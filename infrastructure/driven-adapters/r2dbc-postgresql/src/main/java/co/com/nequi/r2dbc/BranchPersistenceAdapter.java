package co.com.nequi.r2dbc;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.out.BranchRepository;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
import co.com.nequi.r2dbc.repository.BranchDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchRepository {

    private final BranchDataRepository repository;
    private final ResilienceDecorator resilience;

    @Override
    public Mono<Branch> save(Branch branch) {
        return resilience.decorate(
                repository.save(BranchEntityMapper.toEntity(branch))
                        .map(BranchEntityMapper::toDomain));
    }

    @Override
    public Mono<Branch> findById(Long branchId) {
        return resilience.decorate(
                repository.findById(branchId)
                        .map(BranchEntityMapper::toDomain));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        return resilience.decorate(
                repository.save(BranchEntityMapper.toEntity(branch))
                        .map(BranchEntityMapper::toDomain));
    }
}
