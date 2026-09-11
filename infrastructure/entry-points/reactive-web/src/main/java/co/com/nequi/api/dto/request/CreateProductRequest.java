package co.com.nequi.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to create a new product under a branch")
public record CreateProductRequest(
        @Schema(description = "Product name", example = "Coffee", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Available stock units", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stock,
        @Schema(description = "Id of the branch this product belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long branchId) {
}
