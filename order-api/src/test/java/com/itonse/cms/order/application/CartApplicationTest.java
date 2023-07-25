package com.itonse.cms.order.application;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.product.AddProductCartForm;
import com.itonse.cms.order.domain.product.AddProductForm;
import com.itonse.cms.order.domain.product.AddProductItemForm;
import com.itonse.cms.order.domain.redis.Cart;
import com.itonse.cms.order.domain.repository.ProductRepository;
import com.itonse.cms.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
// 테스트용 임베디드 Redis가 아닌 일반 Redis 사용
// 현업에서는 주로 임베디드 Redis를 이용해 테스트 하기 보다는(오버스펙)
// 보통은 Mocking 해서 구간을 테스트 하거나 Mockito(when-then) 을 사용한다고 한다
@SpringBootTest
class CartApplicationTest {

    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_TEST_MODIFY() {

        Long customerId = 100L;

        cartApplication.clearCart(customerId);
        Product p = add_product();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        // 나머지 필드들에 대한 검증
        assertEquals(result.getName(), "나이키 에어포스");
        assertEquals(result.getDescription(), "신발");

        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "나이키 에어포스0");
        assertEquals(result.getProductItems().get(0).getPrice(), 100000);

        customerId = 100L;

        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
        // 데이터가 잘 들어 갔는지
        assertEquals(cart.getMessages().size(), 0);

        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);
    }

    AddProductCartForm makeAddForm(Product p) {
        AddProductCartForm.ProductItem productItem =
                AddProductCartForm.ProductItem.builder()
                        .id(p.getProductItems().get(0).getId())
                        .name(p.getProductItems().get(0).getName())
                        .count(5)
                        .price(20000)
                        .build();

        return AddProductCartForm.builder()
                        .id(p.getId())
                        .sellerId(p.getSellerId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .items(List.of(productItem)).build();
    }

    Product add_product() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스", "신발", 3);

        return productService.addProduct(sellerId, form);  // product를 추가하고, 추가된 객체를 반환

    }

    public static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }

        return AddProductForm.builder()
                .name(name)
                .description(description)
                .itmes(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(100000)
                .count(10)
                .build();
    }
}