package com.itonse.cms.order.service;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.model.ProductItem;
import com.itonse.cms.order.domain.product.AddProductForm;
import com.itonse.cms.order.domain.product.UpdateProductForm;
import com.itonse.cms.order.domain.product.UpdateProductItemForm;
import com.itonse.cms.order.domain.repository.ProductRepository;
import com.itonse.cms.order.exception.CustomException;
import com.itonse.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itonse.cms.order.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.itonse.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form) {
        return productRepository.save(Product.of(sellerId, form));
    }

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form) {
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        product.setName(form.getName());
        product.setDescription(form.getDescription());

        // 아이템들도 같이 변경
        for(UpdateProductItemForm itemForm : form.getItmes()) {   // form 의 모든 아이템들
            ProductItem item = product.getProductItems().stream()   // form 의 아이템 각각에 대해
                    .filter(pi -> pi.getId().equals(itemForm.getId()))   // 아이디 매칭으로 기존 아이템 객체 찾음
                    .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));   // 기존 아이템이 없으면 에러처리
            // 찾은 경우 값을 변경
            item.setName(itemForm.getName());
            item.setPrice(itemForm.getPrice());
            item.setCount(itemForm.getCount());
        }
        return product;
    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId) {
        Product product = productRepository.findBySellerIdAndId(sellerId, productId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        // CascadeType.All 설정을 했기 때문에 product 를 삭제하면 하위의 productItems 도 같이 삭제
        productRepository.delete(product);
    }
}
