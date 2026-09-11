package co.com.nequi.api.handler;

import co.com.nequi.api.dto.response.BranchResponse;
import co.com.nequi.api.dto.request.CreateBranchRequest;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.in.BranchServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchServicePort branchServicePort;
    private final ApiErrorHandler apiErrorHandler;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return request.bodyToMono(CreateBranchRequest.class)
                .map(dto -> Branch.builder()
                        .name(dto.name())
                        .franchiseId(dto.franchiseId())
                        .build())
                .flatMap(branchServicePort::create)
                .map(BranchResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        Long branchId = Long.valueOf(request.pathVariable("branchId"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> branchServicePort.updateName(branchId, dto.name()))
                .map(BranchResponse::from)
                .flatMap(body -> ServerResponse.ok().bodyValue(body))
                .onErrorResume(apiErrorHandler::handle);
    }
}
