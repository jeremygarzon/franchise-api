package co.com.nequi.model.branch.gateways.out;

import co.com.nequi.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(Long branchId);

    Mono<Branch> update(Branch branch);
}
