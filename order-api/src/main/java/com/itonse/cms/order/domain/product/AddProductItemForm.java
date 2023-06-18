package com.itonse.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor  // NoArgs, AllArgs 는 테스트 할때 목킹 작업을 줄임.
@AllArgsConstructor
public class AddProductItemForm {

    private Long productId;
    private String name;
    private Integer price;
    private Integer count;
}
