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
    private Long customerId;
    private List<Product> products = new ArrayList<>();
    private List<String> messages = new ArrayList<>();   // 바뀐 정보를 회원에게 알리는 메세지

    public void addMessage(String message) {
        messages.add(message);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Product {     // 바뀐 데이터를 사용자에게 알려주기 위해 필요
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
