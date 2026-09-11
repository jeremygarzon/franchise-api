package co.com.nequi.api.dto.response;

import co.com.nequi.model.franchise.Franchise;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Franchise representation")
public record FranchiseResponse(
        @Schema(description = "Franchise id", example = "1")
        Long id,
        @Schema(description = "Franchise name", example = "Nequi Franchise")
        String name) {

    public static FranchiseResponse from(Franchise f) {
        return new FranchiseResponse(f.getId(), f.getName());
    }
}
