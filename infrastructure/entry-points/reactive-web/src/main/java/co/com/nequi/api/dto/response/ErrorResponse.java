package co.com.nequi.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error payload")
public record ErrorResponse(
        @Schema(description = "Error code", example = "FRANCHISE_NOT_FOUND")
        String code,
        @Schema(description = "Human readable error message", example = "Franchise not found")
        String message) {
}
