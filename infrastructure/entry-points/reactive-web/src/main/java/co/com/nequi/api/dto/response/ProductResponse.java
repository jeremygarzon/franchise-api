package co.com.nequi.api.dto.response;

import co.com.nequi.model.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product representation")
public record ProductResponse(
        @Schema(description = "Product id", example = "1")
        Long id,
        @Schema(description = "Product name", example = "Coffee")
        String name,
        @Schema(description = "Available stock units", example = "120")
        Integer stock,
        @Schema(description = "Id of the branch this product belongs to", example = "1")
        Long branchId) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getStock(), p.getBranchId());
    }
}
