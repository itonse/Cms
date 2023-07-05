package com.itonse.cms.order.service;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.model.ProductItem;
import com.itonse.cms.order.domain.product.AddProductItemForm;
import com.itonse.cms.order.domain.product.UpdateProductItemForm;
import com.itonse.cms.order.domain.repository.ProductItemRepository;
import com.itonse.cms.order.domain.repository.ProductRepository;
import com.itonse.cms.order.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itonse.cms.order.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;


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
        product.getProductItems().add(productItem);  // Transactional 기능으로 커밋
        return product;
    }

    @Transactional
    public ProductItem updateProductItem(Long sellerId, UpdateProductItemForm form) {
        ProductItem productItem = productItemRepository.findById(form.getId())
                .filter(pi -> pi.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

        productItem.setName(form.getName());
        productItem.setCount(form.getCount());
        productItem.setPrice(form.getPrice());

        // Transactional 로 변경된 productItem 자동 저장

        return productItem;
    }
}
