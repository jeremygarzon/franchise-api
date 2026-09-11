package co.com.nequi.api.handler;

import co.com.nequi.api.dto.request.CreateFranchiseRequest;
import co.com.nequi.api.dto.response.FranchiseResponse;
import co.com.nequi.api.dto.response.ProductResponse;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.in.FranchiseServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseServicePort franchiseServicePort;
    private final ApiErrorHandler apiErrorHandler;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .map(dto -> Franchise.builder().name(dto.name()).build())
                .flatMap(franchiseServicePort::create)
                .map(FranchiseResponse::from)
                .flatMap(body -> ServerResponse.status(HttpStatus.CREATED).bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable("franchiseId"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> franchiseServicePort.updateName(franchiseId, dto.name()))
                .map(FranchiseResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> getHighestStockProducts(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable("franchiseId"));
        return franchiseServicePort.getHighestStockProducts(franchiseId)
                .map(ProductResponse::from)
                .collectList()
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }
}
