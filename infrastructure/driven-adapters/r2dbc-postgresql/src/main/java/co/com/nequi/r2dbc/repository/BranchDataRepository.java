package co.com.nequi.r2dbc.repository;

import co.com.nequi.r2dbc.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BranchDataRepository extends ReactiveCrudRepository<BranchEntity, Long> {
}
