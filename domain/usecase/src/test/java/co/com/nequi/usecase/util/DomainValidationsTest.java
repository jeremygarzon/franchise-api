package co.com.nequi.usecase.util;

import co.com.nequi.model.exception.DomainException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.model.exception.DomainErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;

class DomainValidationsTest {

    @Test
    void requiredEmitsPresentValue() {
        String value = "franchise";

        StepVerifier.create(DomainValidations.required(value, FRANCHISE_REQUIRED))
                .expectNext(value)
                .verifyComplete();
    }

    @Test
    void requiredErrorsWithSuppliedCodeWhenNull() {
        StepVerifier.create(DomainValidations.required(null, FRANCHISE_REQUIRED))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(DomainException.class)
                        .extracting(e -> ((DomainException) e).getErrorCode())
                        .isEqualTo(FRANCHISE_REQUIRED))
                .verify();
    }

    @Test
    void notBlankEmitsNonBlankValue() {
        String value = "branch";

        StepVerifier.create(DomainValidations.notBlank(value, FRANCHISE_NAME_REQUIRED))
                .expectNext(value)
                .verifyComplete();
    }

    @Test
    void notBlankErrorsWithSuppliedCodeWhenNull() {
        StepVerifier.create(DomainValidations.notBlank(null, FRANCHISE_NAME_REQUIRED))
                .expectErrorMatches(error -> error instanceof DomainException
                        && ((DomainException) error).getErrorCode() == FRANCHISE_NAME_REQUIRED)
                .verify();
    }

    @Test
    void notBlankErrorsWithSuppliedCodeWhenWhitespaceOnly() {
        StepVerifier.create(DomainValidations.notBlank("   ", FRANCHISE_NAME_REQUIRED))
                .expectErrorMatches(error -> error instanceof DomainException domainException
                        && domainException.getErrorCode() == FRANCHISE_NAME_REQUIRED)

                .verify();
    }


    @Test
    void nonNegativeEmitsPositiveValue() {
        Integer stock = 5;

        StepVerifier.create(DomainValidations.nonNegative(stock, PRODUCT_STOCK_INVALID))
                .expectNext(stock)
                .verifyComplete();
    }

    @Test
    void nonNegativeEmitsZero() {
        Integer stock = 0;

        StepVerifier.create(DomainValidations.nonNegative(stock, PRODUCT_STOCK_INVALID))
                .expectNext(stock)
                .verifyComplete();
    }

    @Test
    void nonNegativeErrorsWithSuppliedCodeWhenNull() {
        StepVerifier.create(DomainValidations.nonNegative(null, PRODUCT_STOCK_INVALID))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(DomainException.class)
                        .extracting(e -> ((DomainException) e).getErrorCode())
                        .isEqualTo(PRODUCT_STOCK_INVALID))
                .verify();
    }

    @Test
    void nonNegativeErrorsWithSuppliedCodeWhenNegative() {
        StepVerifier.create(DomainValidations.nonNegative(-1, PRODUCT_STOCK_INVALID))
                .expectErrorMatches(error -> error instanceof DomainException
                        && ((DomainException) error).getErrorCode() == PRODUCT_STOCK_INVALID)
                .verify();
    }


    @Test
    void requiredIsLazyAndDoesNotEvaluateBeforeSubscription() {
        Mono<String> pipeline = DomainValidations.required(null, FRANCHISE_REQUIRED);

        StepVerifier.create(pipeline)
                .expectError(DomainException.class)
                .verify();
    }
}
