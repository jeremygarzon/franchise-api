package co.com.nequi.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to update the stock of a product")
public record UpdateStockRequest(
        @Schema(description = "New stock value", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stock) {
}
