package com.itonse.cms.order.service;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.product.AddProductForm;
import com.itonse.cms.order.domain.product.AddProductItemForm;
import com.itonse.cms.order.domain.repository.ProductItemRepository;
import com.itonse.cms.order.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductItemRepository productItemRepository;

    @Test
    void ADD_PRODUCT_TEST() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스1", "신발", 3);

        Product p = productService.addProduct(sellerId, form);  // product를 추가하고, 추가된 객체를 반환

        Product result = productRepository.findWithProductItemsById(p.getId()).get();  // 반환된 객체 정보로 레파지토리를 통해 해당 객체 가져오기 -> 결과적으로 p == result

        assertNotNull(result);

        // (과제)나머지 필드들에 대한 검증
        assertEquals("나이키 에어포스1", result.getName());
        assertEquals(1, result.getSellerId());
        assertEquals("나이키 에어포스12", result.getProductItems().get(2).getName());
        assertEquals(130000, result.getProductItems().get(1).getPrice());

    }

    private static AddProductForm makeProductForm (String name, String description, int itemCount) {
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

    private static final AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(130000)
                .count(1)
                .build();
    }
}