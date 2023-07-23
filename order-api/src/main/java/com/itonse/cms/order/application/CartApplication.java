package com.itonse.cms.order.application;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.model.ProductItem;
import com.itonse.cms.order.domain.product.AddProductCartForm;
import com.itonse.cms.order.domain.redis.Cart;
import com.itonse.cms.order.exception.CustomException;
import com.itonse.cms.order.service.CartService;
import com.itonse.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.itonse.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.itonse.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@RequiredArgsConstructor
@Service
public class CartApplication {

    private final ProductSearchService productSearchService;
    private final CartService cartService;
    
    public Cart addCart(Long customerId, AddProductCartForm form) {

        // 존재하는(유효한) 상품인지 확인
        Product product = productSearchService.getByProductId(form.getId());

        if (product == null) {
            throw new CustomException(NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);    // 고객의 카트 가져오기

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);   // 상품의 수량 부족
        }

        return cartService.addCart(customerId, form);
    }

    // 카트에 추가할 수 있는지 검사
    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        // 상품 검사: form 에 해당하는 상품 찾기
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));   // 상품이 없는 경우 에러처리

        // 수량 검사: 카트와 DB 에서 아이템 수량 가져오기. Map<아이템 id, 수량> (속도를 위해 Map 사용)
        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        // form 을 돌면서 카트에 아이템이 추가될 수 있는지 검사 (아이템의 수량이 충분한지 확인)
        return form.getItems().stream().noneMatch(    // 조건에 일치하지 않는 항목이 하나도 없다면 true 반환
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;    // 수량이 불충분한지 검사
                });
    }
}
