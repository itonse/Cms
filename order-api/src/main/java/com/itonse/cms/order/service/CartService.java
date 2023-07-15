package com.itonse.cms.order.service;

import com.itonse.cms.order.client.RedisClient;
import com.itonse.cms.order.domain.product.AddProductCartForm;
import com.itonse.cms.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final RedisClient redisClient;

    public Cart getCart(Long customerId) {
        return redisClient.get(customerId, Cart.class);
    }

    public Cart addCart(Long customerId, AddProductCartForm form) {

        Cart cart = redisClient.get(customerId, Cart.class);

        // 새 카트 만들기
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // 이전에 같은 상품이 있는지 확인
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(product1 -> product1.getId().equals(form.getId()))
                .findFirst();

        if (productOptional.isPresent()) {   // 존재하는 경우 해상 상품 가져오기
            Cart.Product redisProduct = productOptional.get();
            List<Cart.ProductItem> items = form.getItems().stream()   // 기존 카트에 담은 상품의 옵션들 가져오기
                    .map(Cart.ProductItem::from)
                    .collect(Collectors.toList());
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()    // 검색속도를 위해 맵 사용
                    .collect(Collectors.toMap(item -> item.getId(), item -> item));

            if (!redisProduct.getName().equals(form.getName())) {  // 담았던 상품의 이름이 변경된 경우
                cart.addMessage(redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }
            for (Cart.ProductItem item : items) {   // 모든 상품 정보들 순회
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                if (redisItem == null) {
                    // happy case (Product 에 추가)
                    redisProduct.getItems().add(item);
                } else {
                    // 검사하기
                    if (redisItem.getPrice().equals(item.getPrice())) {    // 상품 가격이 변동된 경우
                        cart.addMessage(redisProduct.getName() + item.getName() + "의 가격이 변경되었습니다. 확인 부탁드립니다.");
                    }
                    redisItem.setCount(redisItem.getCount() + item.getCount());    // 상품 수량 변경 (추가)
                }
            }
        } else {   // 기존 장바구니에 상품이 존재하지 않는 경우, 새로 추가
            Cart.Product product = Cart.Product.from(form);   ///
            cart.getProducts().add(product);
        }

        redisClient.put(customerId, cart);
        return cart;
    }
}
