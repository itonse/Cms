package com.itonse.cms.order.domain.model;

import com.itonse.cms.order.domain.product.AddProductItemForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity{  // Product 는 상품 (컨버스 척 90) ProductItem 은 상품옵션 (245mm)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;    // 어떤 기준으로 search 를 할지 모르기 때문에 일단 추가

    @Audited    // 클래스 전체가 아닌 변할 수 있는 항목에만 추가
    private String name;  // ex) 245mm

    @Audited
    private Integer price;

    private Integer count;  // 수량

    @ManyToOne(cascade = CascadeType.ALL)   // product (one) : productItem (many) 다대일 양방향 관계
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductItem of(Long sellerId, AddProductItemForm form) {
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }
}
