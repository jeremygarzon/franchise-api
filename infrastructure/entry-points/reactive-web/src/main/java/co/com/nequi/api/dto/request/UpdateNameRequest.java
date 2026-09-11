package co.com.nequi.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to update the name of an entity")
public record UpdateNameRequest(
        @Schema(description = "New name", example = "Updated name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name) {
}
