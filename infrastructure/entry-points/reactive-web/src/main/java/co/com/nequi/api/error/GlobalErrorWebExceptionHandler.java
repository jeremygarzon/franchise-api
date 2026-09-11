package co.com.nequi.api.error;

import co.com.nequi.api.dto.response.ErrorResponse;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.all;

@Component
@Order(-2)
@Log4j2
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          WebProperties webProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(all(), this::renderError);
    }

    private Mono<ServerResponse> renderError(ServerRequest request) {
        Throwable error = getError(request);
        String path = request.path();

        if (error instanceof DomainException de) {
            log.warn("Domain error [{}] on {}: {}", de.getErrorCode().name(), path, de.getMessage());
            return ServerResponse.status(statusFor(de.getErrorCode()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse(de.getErrorCode().name(), de.getMessage()));
        }

        if (error instanceof org.springframework.web.ErrorResponse er) {
            HttpStatusCode status = er.getStatusCode();
            log.warn("HTTP error [{}] on {}: {}", status.value(), path, reasonFor(status));
            return ServerResponse.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse(codeFor(status), reasonFor(status)));
        }

        log.error("Unexpected error on {}", path, error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse("INTERNAL_ERROR", "Unexpected error"));
    }

    private HttpStatus statusFor(DomainErrorCode code) {
        return switch (code.getType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case VALIDATION -> HttpStatus.BAD_REQUEST;
        };
    }

    private String codeFor(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.name() : "HTTP_" + status.value();
    }

    private String reasonFor(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.getReasonPhrase() : "Request failed";
    }
}
