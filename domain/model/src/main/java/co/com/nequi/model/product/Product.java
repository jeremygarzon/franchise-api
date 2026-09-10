package co.com.nequi.model.product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Product {

    private final Long id;
    private final String name;
    private final Integer stock;
    private final Long branchId;
}
