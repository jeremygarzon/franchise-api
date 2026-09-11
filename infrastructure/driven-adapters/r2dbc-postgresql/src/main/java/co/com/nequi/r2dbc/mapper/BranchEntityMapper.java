package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.r2dbc.entity.BranchEntity;

public final class BranchEntityMapper {

    private BranchEntityMapper() {
    }

    public static BranchEntity toEntity(Branch domain) {
        if (domain == null) {
            return null;
        }
        return BranchEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .franchiseId(domain.getFranchiseId())
                .build();
    }

    public static Branch toDomain(BranchEntity entity) {
        if (entity == null) {
            return null;
        }
        return Branch.builder()
                .id(entity.getId())
                .name(entity.getName())
                .franchiseId(entity.getFranchiseId())
                .build();
    }
}
