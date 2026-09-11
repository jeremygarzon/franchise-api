package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entity.BranchEntity;
import co.com.nequi.r2dbc.entity.FranchiseEntity;
import co.com.nequi.r2dbc.entity.ProductEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperRoundTripTest {

    @Test
    void franchiseRoundTripFromDomainPreservesAllFields() {
        Franchise original = Franchise.builder().id(1L).name("Acme").build();

        Franchise result = FranchiseEntityMapper.toDomain(FranchiseEntityMapper.toEntity(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
    }

    @Test
    void franchiseRoundTripFromEntityPreservesAllFields() {
        FranchiseEntity original = FranchiseEntity.builder().id(9L).name("Globex").build();

        FranchiseEntity result = FranchiseEntityMapper.toEntity(FranchiseEntityMapper.toDomain(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
    }

    @Test
    void franchiseMapperHandlesNullId() {
        Franchise idless = Franchise.builder().name("Fresh").build();

        Franchise result = FranchiseEntityMapper.toDomain(FranchiseEntityMapper.toEntity(idless));

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Fresh");
    }

    @Test
    void franchiseMapperMapsNullToNull() {
        assertThat(FranchiseEntityMapper.toEntity(null)).isNull();
        assertThat(FranchiseEntityMapper.toDomain(null)).isNull();
    }


    @Test
    void branchRoundTripFromDomainPreservesAllFields() {
        Branch original = Branch.builder().id(2L).name("Downtown").franchiseId(1L).build();

        Branch result = BranchEntityMapper.toDomain(BranchEntityMapper.toEntity(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getFranchiseId()).isEqualTo(original.getFranchiseId());
    }

    @Test
    void branchRoundTripFromEntityPreservesAllFields() {
        BranchEntity original = BranchEntity.builder().id(3L).name("Uptown").franchiseId(1L).build();

        BranchEntity result = BranchEntityMapper.toEntity(BranchEntityMapper.toDomain(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getFranchiseId()).isEqualTo(original.getFranchiseId());
    }

    @Test
    void branchMapperMapsNullToNull() {
        assertThat(BranchEntityMapper.toEntity(null)).isNull();
        assertThat(BranchEntityMapper.toDomain(null)).isNull();
    }

    @Test
    void productRoundTripFromDomainPreservesAllFields() {
        Product original = Product.builder().id(4L).name("Widget").stock(25).branchId(2L).build();

        Product result = ProductEntityMapper.toDomain(ProductEntityMapper.toEntity(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getStock()).isEqualTo(original.getStock());
        assertThat(result.getBranchId()).isEqualTo(original.getBranchId());
    }

    @Test
    void productRoundTripFromEntityPreservesAllFields() {
        ProductEntity original = ProductEntity.builder().id(5L).name("Gadget").stock(0).branchId(2L).build();

        ProductEntity result = ProductEntityMapper.toEntity(ProductEntityMapper.toDomain(original));

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getStock()).isEqualTo(original.getStock());
        assertThat(result.getBranchId()).isEqualTo(original.getBranchId());
    }

    @Test
    void productMapperMapsNullToNull() {
        assertThat(ProductEntityMapper.toEntity(null)).isNull();
        assertThat(ProductEntityMapper.toDomain(null)).isNull();
    }
}
