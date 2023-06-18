package com.itonse.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor  // NoArgs, AllArgs 는 테스트 할때 목킹 작업을 줄임.
@AllArgsConstructor
public class AddProductForm {

    private String name;
    private String description;
    private List<AddProductItemForm> itmes;
}
