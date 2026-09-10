package co.com.nequi.model.exception;

import lombok.Getter;

@Getter
public enum DomainErrorCode {

    FRANCHISE_REQUIRED("Franchise is required", ErrorType.VALIDATION),
    FRANCHISE_NAME_REQUIRED("Franchise name cannot be empty", ErrorType.VALIDATION),
    BRANCH_REQUIRED("Branch is required", ErrorType.VALIDATION),
    BRANCH_NAME_REQUIRED("Branch name cannot be empty", ErrorType.VALIDATION),
    FRANCHISE_ID_REQUIRED("Franchise id is required", ErrorType.VALIDATION),
    PRODUCT_REQUIRED("Product is required", ErrorType.VALIDATION),
    PRODUCT_NAME_REQUIRED("Product name cannot be empty", ErrorType.VALIDATION),
    PRODUCT_STOCK_INVALID("Product stock cannot be negative", ErrorType.VALIDATION),
    BRANCH_ID_REQUIRED("Branch id is required", ErrorType.VALIDATION),

    FRANCHISE_NOT_FOUND("Franchise not found", ErrorType.NOT_FOUND),
    BRANCH_NOT_FOUND("Branch not found", ErrorType.NOT_FOUND),
    PRODUCT_NOT_FOUND("Product not found", ErrorType.NOT_FOUND);

    private final String message;
    private final ErrorType type;

    DomainErrorCode(String message, ErrorType type) {
        this.message = message;
        this.type = type;
    }

    public enum ErrorType {
        VALIDATION,
        NOT_FOUND
    }
}
