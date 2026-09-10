package co.com.nequi.model.branch.gateways.in;

import co.com.nequi.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchServicePort {

    Mono<Branch> create(Branch branch);

    Mono<Branch> updateName(Long branchId, String name);
}
