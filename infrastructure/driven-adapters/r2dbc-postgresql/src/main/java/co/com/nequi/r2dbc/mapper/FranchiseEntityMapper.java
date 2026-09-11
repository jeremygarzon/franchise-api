package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.r2dbc.entity.FranchiseEntity;

public final class FranchiseEntityMapper {

    private FranchiseEntityMapper() {
    }

    public static FranchiseEntity toEntity(Franchise domain) {
        if (domain == null) {
            return null;
        }
        return FranchiseEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    public static Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Franchise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
