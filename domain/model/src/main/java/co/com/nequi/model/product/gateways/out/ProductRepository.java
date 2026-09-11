package co.com.nequi.model.product.gateways.out;

import co.com.nequi.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> save(Product product);

    Mono<Product> findById(Long productId);

    Mono<Product> update(Product product);

    Mono<Void> deleteById(Long productId);

    Flux<Product> findHighestStockProductsByFranchiseId(Long franchiseId);
}
