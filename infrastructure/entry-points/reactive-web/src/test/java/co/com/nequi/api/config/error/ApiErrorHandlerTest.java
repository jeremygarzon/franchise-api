package co.com.nequi.api.config.error;

import co.com.nequi.api.error.ApiErrorHandler;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorHandlerTest {

    private final ApiErrorHandler handler = new ApiErrorHandler();

    @Test
    void validationErrorsMapToBadRequest() {
        Arrays.stream(DomainErrorCode.values())
                .filter(code -> code.getType() == DomainErrorCode.ErrorType.VALIDATION)
                .forEach(code -> assertStatus(new DomainException(code), HttpStatus.BAD_REQUEST));
    }

    @Test
    void notFoundErrorsMapToNotFound() {
        Arrays.stream(DomainErrorCode.values())
                .filter(code -> code.getType() == DomainErrorCode.ErrorType.NOT_FOUND)
                .forEach(code -> assertStatus(new DomainException(code), HttpStatus.NOT_FOUND));
    }

    @Test
    void unexpectedErrorsMapToInternalServerError() {
        assertStatus(new RuntimeException("boom"), HttpStatus.INTERNAL_SERVER_ERROR);
        assertStatus(new IllegalStateException("bad state"), HttpStatus.INTERNAL_SERVER_ERROR);
        assertStatus(new IOException("io failure"), HttpStatus.INTERNAL_SERVER_ERROR);
        assertStatus(new NullPointerException(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void assertStatus(Throwable error, HttpStatus expected) {
        Mono<ServerResponse> response = handler.handle(error);

        StepVerifier.create(response)
                .assertNext(serverResponse ->
                        assertThat(serverResponse.statusCode()).isEqualTo(expected))
                .verifyComplete();
    }
}
