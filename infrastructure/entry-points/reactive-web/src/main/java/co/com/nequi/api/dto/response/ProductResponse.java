package co.com.nequi.api.dto.response;

import co.com.nequi.model.product.Product;

public record ProductResponse(Long id, String name, Integer stock, Long branchId) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getStock(), p.getBranchId());
    }
}
