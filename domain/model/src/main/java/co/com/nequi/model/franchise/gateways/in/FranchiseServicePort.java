package co.com.nequi.model.franchise.gateways.in;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {

    Mono<Franchise> create(Franchise franchise);

    Mono<Franchise> updateName(Long franchiseId, String name);

    Flux<Product> getHighestStockProducts(Long franchiseId);
}
