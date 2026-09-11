package co.com.nequi.r2dbc;

import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.out.ProductRepository;
import co.com.nequi.r2dbc.config.ResilienceDecorator;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import co.com.nequi.r2dbc.repository.ProductDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductDataRepository repository;
    private final ResilienceDecorator resilience;

    @Override
    public Mono<Product> save(Product product) {
        return resilience.decorate(
                repository.save(ProductEntityMapper.toEntity(product))
                        .map(ProductEntityMapper::toDomain));
    }

    @Override
    public Mono<Product> findById(Long productId) {
        return resilience.decorate(
                repository.findById(productId)
                        .map(ProductEntityMapper::toDomain));
    }

    @Override
    public Mono<Product> update(Product product) {
        return resilience.decorate(
                repository.save(ProductEntityMapper.toEntity(product))
                        .map(ProductEntityMapper::toDomain));
    }

    @Override
    public Mono<Void> deleteById(Long productId) {
        return resilience.decorate(repository.deleteById(productId));
    }

    @Override
    public Flux<Product> findHighestStockProductsByFranchiseId(Long franchiseId) {
        return resilience.decorate(
                repository.findHighestStockProductsByFranchiseId(franchiseId)
                        .map(ProductEntityMapper::toDomain));
    }
}
