package co.com.nequi.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("franchise")
public class FranchiseEntity {

    @Id
    private Long id;

    @Column("name")
    private String name;
}
