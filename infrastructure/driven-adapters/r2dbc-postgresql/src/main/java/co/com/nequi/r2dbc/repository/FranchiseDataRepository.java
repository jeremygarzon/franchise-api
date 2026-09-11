package co.com.nequi.r2dbc.repository;

import co.com.nequi.r2dbc.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseDataRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
}
