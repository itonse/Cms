package com.itonse.cms.order.domain.model;

import com.itonse.cms.order.domain.product.AddProductForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Audited // 엔티티의 데이터가 변할 때 마다 그 내용들을 product_aud 엔티티에 저장 -> 상품이나 상품에 대한 옵션들이 바뀐 것을 트래킹.
@AuditOverride(forClass = BaseEntity.class)
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<ProductItem> productItems = new ArrayList<>();

    public static Product of(Long sellerId, AddProductForm form) {
        return Product.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .productItems(form.getItmes().stream()
                        .map(piFrom->ProductItem.of(sellerId,piFrom))
                        .collect(Collectors.toList()))
                .build();
    }
}
