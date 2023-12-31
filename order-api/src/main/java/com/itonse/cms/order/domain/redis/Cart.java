package com.itonse.cms.order.domain.redis;

import com.itonse.cms.order.domain.product.AddProductCartForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RedisHash("Cart")    // 추후 해당 데이터에 대한 key 가 생성될 때 prefix 지정
public class Cart {
    @Id
    private Long customerId;  // 계정당 하나의 장바구니만 가질 수 있다
    private List<Product> products = new ArrayList<>();
    private List<String> messages = new ArrayList<>();   // 바뀐 정보를 회원에게 알리는 메세지

    public void addMessage(String message) {
        messages.add(message);
    }

    public Cart(Long customerId) {    // 생성자 추가 (products 와 messages 는 자동으로 초기화)
        this.customerId = customerId;
    }

    /**
     * 상품(+아이템)의 바뀐 데이터를 레디스에 저장했다가, 고객이 장바구니를 볼 때 확인할 수 있도록 할 것
     */

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Product {
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> items = new ArrayList<>();

        public static Product from(AddProductCartForm form) {
            return Product.builder()
                    .id(form.getId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .description(form.getDescription())
                    .items(form.getItems().stream()
                            .map(ProductItem::from)
                            .collect(Collectors.toList()))
                    .build();
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ProductItem{
        private Long id;
        private String name;
        private Integer count;
        private Integer price;

        public static ProductItem from(AddProductCartForm.ProductItem form) {
            return ProductItem.builder()
                    .id(form.getId())
                    .name(form.getName())
                    .count(form.getCount())
                    .price(form.getPrice())
                    .build();
        }
    }

}
