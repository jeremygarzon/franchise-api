package co.com.nequi.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to create a new franchise")
public record CreateFranchiseRequest(
        @Schema(description = "Franchise name", example = "Nequi Franchise", requiredMode = Schema.RequiredMode.REQUIRED)
        String name) {
}
