package co.com.nequi.model.exception.exception;

import co.com.nequi.model.exception.DomainErrorCode.ErrorType;
import co.com.nequi.model.exception.DomainErrorCode;
import co.com.nequi.model.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DomainErrorModelTest {

    private static final Set<DomainErrorCode> VALIDATION_CODES = EnumSet.of(
            DomainErrorCode.FRANCHISE_REQUIRED,
            DomainErrorCode.FRANCHISE_NAME_REQUIRED,
            DomainErrorCode.BRANCH_REQUIRED,
            DomainErrorCode.BRANCH_NAME_REQUIRED,
            DomainErrorCode.FRANCHISE_ID_REQUIRED,
            DomainErrorCode.PRODUCT_REQUIRED,
            DomainErrorCode.PRODUCT_NAME_REQUIRED,
            DomainErrorCode.PRODUCT_STOCK_INVALID,
            DomainErrorCode.BRANCH_ID_REQUIRED
    );

    private static final Set<DomainErrorCode> NOT_FOUND_CODES = EnumSet.of(
            DomainErrorCode.FRANCHISE_NOT_FOUND,
            DomainErrorCode.BRANCH_NOT_FOUND,
            DomainErrorCode.PRODUCT_NOT_FOUND
    );

    @Test
    void validationCodesReportValidationType() {
        assertThat(VALIDATION_CODES)
                .allSatisfy(code -> assertThat(code.getType()).isEqualTo(ErrorType.VALIDATION));
    }

    @Test
    void notFoundCodesReportNotFoundType() {
        assertThat(NOT_FOUND_CODES)
                .allSatisfy(code -> assertThat(code.getType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void everyCodeIsClassifiedIntoExactlyOneFamily() {
        Set<DomainErrorCode> union = EnumSet.copyOf(VALIDATION_CODES);
        union.addAll(NOT_FOUND_CODES);

        assertThat(EnumSet.copyOf(VALIDATION_CODES))
                .doesNotContainAnyElementsOf(NOT_FOUND_CODES);

        assertThat(union).containsExactlyInAnyOrder(DomainErrorCode.values());
    }

    @Test
    void everyCodeHasMessageAndType() {
        assertThat(DomainErrorCode.values()).allSatisfy(code -> {
            assertThat(code.getMessage()).isNotBlank();
            assertThat(code.getType()).isNotNull();
        });
    }

    @Test
    void domainExceptionCarriesErrorCodeAndMessage() {
        DomainException exception = new DomainException(DomainErrorCode.FRANCHISE_NOT_FOUND);

        assertThat(exception.getErrorCode()).isEqualTo(DomainErrorCode.FRANCHISE_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(DomainErrorCode.FRANCHISE_NOT_FOUND.getMessage());
    }

    @Test
    void domainExceptionMessageMatchesCodeForEveryCode() {
        assertThat(DomainErrorCode.values()).allSatisfy(code -> {
            DomainException exception = new DomainException(code);
            assertThat(exception.getErrorCode()).isEqualTo(code);
            assertThat(exception.getMessage()).isEqualTo(code.getMessage());
        });
    }

}
