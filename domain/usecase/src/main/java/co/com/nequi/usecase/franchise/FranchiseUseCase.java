package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.in.FranchiseServicePort;
import co.com.nequi.model.franchise.gateways.out.FranchiseRepository;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.out.ProductRepository;
import co.com.nequi.usecase.util.DomainValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        return DomainValidations.required(franchise, DomainErrorCode.FRANCHISE_REQUIRED)
                .flatMap(value ->
                        DomainValidations.notBlank(value.getName(), DomainErrorCode.FRANCHISE_NAME_REQUIRED)
                                .thenReturn(value)
                                .flatMap(franchiseRepository::save));
    }

    @Override
    public Mono<Franchise> updateName(Long franchiseId, String name) {
        return DomainValidations.notBlank(name, DomainErrorCode.FRANCHISE_NAME_REQUIRED)
                .then(existingFranchise(franchiseId))
                        .map(f -> Franchise.builder()
                                .id(f.getId())
                                .name(name)
                                .build())
                        .flatMap(franchiseRepository::update);
    }

    @Override
    public Flux<Product> getHighestStockProducts(Long franchiseId) {
        return existingFranchise(franchiseId)
                .flatMapMany(f ->
                        productRepository.findHighestStockProductsByFranchiseId(franchiseId));
    }

    private Mono<Franchise> existingFranchise(Long id) {
        return DomainValidations.required(id, DomainErrorCode.FRANCHISE_ID_REQUIRED)
                .flatMap(franchiseRepository::findById)
                .switchIfEmpty(Mono.error(new DomainException(DomainErrorCode.FRANCHISE_NOT_FOUND)));
    }
}
