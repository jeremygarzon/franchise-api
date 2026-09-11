package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.gateways.out.BranchRepository;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.in.ProductServicePort;
import co.com.nequi.model.product.gateways.out.ProductRepository;
import co.com.nequi.usecase.util.DomainValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase implements ProductServicePort {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Override
    public Mono<Product> create(Product product) {
        return DomainValidations.required(product, DomainErrorCode.PRODUCT_REQUIRED)
                .flatMap(value ->
                        DomainValidations.notBlank(product.getName(), DomainErrorCode.PRODUCT_NAME_REQUIRED)
                                .then(DomainValidations.nonNegative(value.getStock(), DomainErrorCode.PRODUCT_STOCK_INVALID))
                                .then(DomainValidations.required(value.getBranchId(), DomainErrorCode.BRANCH_ID_REQUIRED))
                                .flatMap(branchRepository::findById)
                                .switchIfEmpty(Mono.error(new DomainException(DomainErrorCode.BRANCH_NOT_FOUND)))
                                .thenReturn(value))
                .flatMap(productRepository::save);
    }

    @Override
    public Mono<Void> delete(Long productId) {
        return existingProduct(productId)
                .flatMap(p -> productRepository.deleteById(p.getId()))
                .then();
    }

    @Override
    public Mono<Product> updateStock(Long productId, Integer stock) {
        return DomainValidations.nonNegative(stock, DomainErrorCode.PRODUCT_STOCK_INVALID)
                .then(existingProduct(productId))
                .map(p -> Product.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .stock(stock)
                        .branchId(p.getBranchId())
                        .build())
                .flatMap(productRepository::update);
    }

    @Override
    public Mono<Product> updateName(Long productId, String name) {
        return DomainValidations.notBlank(name, DomainErrorCode.PRODUCT_NAME_REQUIRED)
                .then(existingProduct(productId))
                .map(p -> Product.builder()
                        .id(p.getId())
                        .name(name)
                        .stock(p.getStock())
                        .branchId(p.getBranchId())
                        .build())
                .flatMap(productRepository::update);
    }

    private Mono<Product> existingProduct(Long id) {
        return DomainValidations.required(id, DomainErrorCode.PRODUCT_REQUIRED)
                .flatMap(productRepository::findById)
                .switchIfEmpty(Mono.error(new DomainException(DomainErrorCode.PRODUCT_NOT_FOUND)));
    }
}
