package co.com.nequi.api.dto.request;

public record CreateProductRequest(String name, Integer stock, Long branchId) {
}
