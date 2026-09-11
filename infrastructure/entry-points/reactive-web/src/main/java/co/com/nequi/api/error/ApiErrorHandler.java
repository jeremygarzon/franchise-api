package co.com.nequi.api.error;

import co.com.nequi.api.dto.response.ErrorResponse;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class ApiErrorHandler {

    public Mono<ServerResponse> handle(Throwable error) {
        if (error instanceof DomainException de) {
            log.warn("Domain error [{}]: {}", de.getErrorCode().name(), de.getMessage());
            HttpStatus status = statusFor(de.getErrorCode());
            return ServerResponse.status(status)
                    .bodyValue(new ErrorResponse(de.getErrorCode().name(), de.getMessage()));
        }
        if (error instanceof org.springframework.web.ErrorResponse er) {
            HttpStatusCode status = er.getStatusCode();
            log.warn("HTTP error [{}]: {}", status.value(), reasonFor(status));
            return ServerResponse.status(status)
                    .bodyValue(new ErrorResponse(codeFor(status), reasonFor(status)));
        }
        log.error("Unexpected error handling request", error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
