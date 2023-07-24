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

import java.util.ArrayList;
import java.util.List;
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

    // 1. 장바구니에 상품을 추가 했다.
    // 2. 상품의 가격이나 수량이 변동 된다.
    public Cart getCart(Long customerId) {
        Cart cart =  refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart();

        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        // 메세지 없는 것
        cartService.putCart(customerId, cart);
        return returnCart;

        // 3. 메세지를 보고 난 다음에는, 이미 본 메세지는 스팸이 되기 때문에 '제거'한다.
    }

    private Cart refreshCart(Cart cart) {
        // 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크하고
        // 그에 맞는 알람을 제공해준다.
        // 2. 상품의 수량, 가격을 임의로 변경한다.
        Map<Long, Product> productMap = productSearchService.getListByProductIds(cart.getProducts().stream()
                        .map(Cart.Product::getId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i < cart.getProducts().size(); i++) {

            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());
            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            // 아이템 1,2,3
            List<String> tmpMessages = new ArrayList<>();   // 메세지 임시저장소

            for (int j = 0; j < cartProduct.getItems().size(); j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());
                if (pi == null) {
                    cart.getProducts().remove(cartProductItem);
                    j--;
                    tmpMessages.add(cartProductItem.getName() + "옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChanged = false;
                boolean isCountNotEnough = false;

                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setPrice(pi.getCount());
                }
                if (isPriceChanged && isCountNotEnough) {
                    tmpMessages.add(cartProductItem.getName() + "가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChanged) {
                    tmpMessages.add(cartProductItem.getName() + "가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tmpMessages.add(cartProductItem.getName() + "수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }
            if (cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            }
            if (tmpMessages.size() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
                for (String message : tmpMessages) {
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
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
