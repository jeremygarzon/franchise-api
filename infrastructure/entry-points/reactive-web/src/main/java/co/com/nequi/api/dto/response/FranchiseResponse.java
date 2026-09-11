package co.com.nequi.api.dto.response;

import co.com.nequi.model.franchise.Franchise;

public record FranchiseResponse(Long id, String name) {

    public static FranchiseResponse from(Franchise f) {
        return new FranchiseResponse(f.getId(), f.getName());
    }
}
