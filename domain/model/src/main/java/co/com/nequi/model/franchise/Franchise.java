package co.com.nequi.model.franchise;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Franchise {

    private final Long id;
    private final String name;
}
