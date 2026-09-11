package co.com.nequi.model.franchise.gateways.out;

import co.com.nequi.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(Long franchiseId);

    Mono<Franchise> update(Franchise franchise);
}
