package co.com.nequi.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to create a new branch under a franchise")
public record CreateBranchRequest(
        @Schema(description = "Branch name", example = "Downtown Branch", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Id of the franchise this branch belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long franchiseId) {
}
