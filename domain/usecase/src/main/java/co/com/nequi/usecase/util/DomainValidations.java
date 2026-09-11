package co.com.nequi.usecase.util;

import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import reactor.core.publisher.Mono;

public final class DomainValidations {

    private DomainValidations() {
    }

    public static <T> Mono<T> required(T value, DomainErrorCode code) {
        return Mono.justOrEmpty(value)
                .switchIfEmpty(Mono.error(new DomainException(code)));
    }

    public static Mono<String> notBlank(String name, DomainErrorCode code) {
        return Mono.justOrEmpty(name)
                .filter(n -> !n.isBlank())
                .switchIfEmpty(Mono.error(new DomainException(code)));
    }

    public static Mono<Integer> nonNegative(Integer stock, DomainErrorCode code) {
        return Mono.justOrEmpty(stock)
                .filter(s -> s >= 0)
                .switchIfEmpty(Mono.error(new DomainException(code)));
    }
}
