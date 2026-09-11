package co.com.nequi.r2dbc.repository;

import co.com.nequi.r2dbc.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductDataRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    @Query("""
            SELECT p.id, p.name, p.stock, p.branch_id
            FROM (
                SELECT prod.*,
                       ROW_NUMBER() OVER (PARTITION BY prod.branch_id ORDER BY prod.stock DESC, prod.id ASC) AS rn
                FROM product prod
                JOIN branch b ON b.id = prod.branch_id
                WHERE b.franchise_id = :franchiseId
            ) p
            WHERE p.rn = 1
            """)
    Flux<ProductEntity> findHighestStockProductsByFranchiseId(@Param("franchiseId") Long franchiseId);
}
