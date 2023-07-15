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

    private final ProductSearchService productSearchService;  // 상품이 사라질 수도 있으니 추가
    private final CartService cartService;
    
    public Cart addCart(Long customerId, AddProductCartForm form) {

        Product product = productSearchService.getByProductId(form.getId());

        if (product == null) {
            throw new CustomException(NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);   // 상품의 수량 부족
        }

        return cartService.addCart(customerId, form);
    }

    // 카트에 추가할 수 있는지 검사
    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));   // 상품이 없는 경우 에러처리

        // 카트의 아이템 꺼내기
        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()   // 속도를 위해 Map 사용
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        // form 을 돌면서 카트에 아이템이 추가될 수 있는지 검사 (아이템의 수량이 충분한지 확인)
        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;
                });
    }
}
