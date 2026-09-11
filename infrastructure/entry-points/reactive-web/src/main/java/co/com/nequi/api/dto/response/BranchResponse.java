package co.com.nequi.api.dto.response;

import co.com.nequi.model.branch.Branch;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Branch representation")
public record BranchResponse(
        @Schema(description = "Branch id", example = "1")
        Long id,
        @Schema(description = "Branch name", example = "Downtown Branch")
        String name,
        @Schema(description = "Id of the franchise this branch belongs to", example = "1")
        Long franchiseId) {

    public static BranchResponse from(Branch b) {
        return new BranchResponse(b.getId(), b.getName(), b.getFranchiseId());
    }
}
