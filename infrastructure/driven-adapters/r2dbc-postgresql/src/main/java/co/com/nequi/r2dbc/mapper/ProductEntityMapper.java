package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entity.ProductEntity;

public final class ProductEntityMapper {

    private ProductEntityMapper() {
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .stock(domain.getStock())
                .branchId(domain.getBranchId())
                .build();
    }

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .stock(entity.getStock())
                .branchId(entity.getBranchId())
                .build();
    }
}
