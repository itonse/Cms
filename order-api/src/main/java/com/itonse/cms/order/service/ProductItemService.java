package com.itonse.cms.order.service;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.model.ProductItem;
import com.itonse.cms.order.domain.product.AddProductItemForm;
import com.itonse.cms.order.domain.repository.ProductRepository;
import com.itonse.cms.order.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itonse.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.itonse.cms.order.exception.ErrorCode.SAME_ITEM_NAME;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;


    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form) {
        // 먼저 Product 가 있는지 확인
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        // 이미 같은 상품아이템 옵션이 있는지 체크
        if (product.getProductItems().stream()
                .anyMatch(item -> item.getName().equals(form.getName()))) {
            throw new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);  // 레파지토리를 통해서 저장 안하고, add(리스트 기능) 를 통해 저장
        return product;
    }
}
