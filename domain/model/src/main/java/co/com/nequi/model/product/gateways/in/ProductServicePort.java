package co.com.nequi.model.product.gateways.in;

import co.com.nequi.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductServicePort {

    Mono<Product> create(Product product);

    Mono<Void> delete(Long productId);

    Mono<Product> updateStock(Long productId, Integer stock);

    Mono<Product> updateName(Long productId, String name);
}
