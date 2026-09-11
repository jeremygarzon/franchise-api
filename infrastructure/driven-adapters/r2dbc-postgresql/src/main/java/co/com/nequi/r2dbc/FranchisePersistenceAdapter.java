package co.com.nequi.r2dbc;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.out.FranchiseRepository;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.mapper.FranchiseEntityMapper;
import co.com.nequi.r2dbc.repository.FranchiseDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchiseRepository {

    private final FranchiseDataRepository repository;
    private final ResilienceDecorator resilience;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return resilience.decorate(
                repository.save(FranchiseEntityMapper.toEntity(franchise))
                        .map(FranchiseEntityMapper::toDomain));
    }

    @Override
    public Mono<Franchise> findById(Long franchiseId) {
        return resilience.decorate(
                repository.findById(franchiseId)
                        .map(FranchiseEntityMapper::toDomain));
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return resilience.decorate(
                repository.save(FranchiseEntityMapper.toEntity(franchise))
                        .map(FranchiseEntityMapper::toDomain));
    }
}
