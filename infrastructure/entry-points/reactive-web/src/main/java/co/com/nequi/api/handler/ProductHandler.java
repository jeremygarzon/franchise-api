package co.com.nequi.api.handler;

import co.com.nequi.api.dto.request.CreateProductRequest;
import co.com.nequi.api.dto.response.ProductResponse;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.dto.request.UpdateStockRequest;
import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.in.ProductServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductServicePort productServicePort;
    private final ApiErrorHandler apiErrorHandler;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(CreateProductRequest.class)
                .map(dto -> Product.builder()
                        .name(dto.name())
                        .stock(dto.stock())
                        .branchId(dto.branchId())
                        .build())
                .flatMap(productServicePort::create)
                .map(ProductResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return productServicePort.delete(productId)
                .then(ServerResponse.ok().build())
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> productServicePort.updateStock(productId, dto.stock()))
                .map(ProductResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> productServicePort.updateName(productId, dto.name()))
                .map(ProductResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }
}
