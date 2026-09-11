package co.com.nequi.api.dto.response;

import co.com.nequi.model.branch.Branch;

public record BranchResponse(Long id, String name, Long franchiseId) {

    public static BranchResponse from(Branch b) {
        return new BranchResponse(b.getId(), b.getName(), b.getFranchiseId());
    }
}
