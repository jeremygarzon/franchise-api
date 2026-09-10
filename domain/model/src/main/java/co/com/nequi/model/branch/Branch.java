package co.com.nequi.model.branch;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Branch {

    private final Long id;
    private final String name;
    private final Long franchiseId;
}
