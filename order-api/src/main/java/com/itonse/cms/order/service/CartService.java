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

    private final RedisClient redisClient;     // 레디스 클라이언트 사용

    public Cart getCart(Long customerId) {
        Cart cart = redisClient.get(customerId, Cart.class);
        return cart != null ? cart : new Cart();
    }

    public Cart putCart(Long customerId, Cart cart) {
        redisClient.put(customerId, cart);
        return cart;
    }

    public Cart addCart(Long customerId, AddProductCartForm form) {

        Cart cart = redisClient.get(customerId, Cart.class);

        // 카트가 없을 경우 새 카트 만들기
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // 카트에 이미 상품이 있는지 확인
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(product1 -> product1.getId().equals(form.getId()))
                .findFirst();

        if (productOptional.isPresent()) {   // 이미 카트에 있는 경우
            Cart.Product redisProduct = productOptional.get();   // 카트에서 해당 상품 가져오기

            if (!redisProduct.getName().equals(form.getName())) {  // 담았던 상품의 이름이 변경된 경우
                cart.addMessage(redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }

            List<Cart.ProductItem> items = form.getItems().stream()   // form 에서 상품의 아이템들 가져오기
                    .map(Cart.ProductItem::from)
                    .collect(Collectors.toList());

            // 카트에서 꺼낸 상품의 아이템들을 Map 형태로 변환 (key: 아이템의 id, Value: item 객체)
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()    // 검색속도를 위해 맵 사용
                    .collect(Collectors.toMap(item -> item.getId(), item -> item));

            // 카트에서 꺼낸 상품(+아이템) 의 정보가 변경된 경우 사용자에게 알리기.

            for (Cart.ProductItem item : items) {   // form 에 입력된 상품의 아이템들을 순회
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());    // 현재 순회중인 아이템의 id에 해당하는 카트의 아이템을 가져온다

                if (redisItem == null) {    // 카트에 있는 상품의 아이템에 form 의 상품의 아이템이 없는 경우
                    // happy case (Product 에 새로 추가)
                    redisProduct.getItems().add(item);
                } else {    // 이미 존재하는 경우
                    // 검사하기
                    if (redisItem.getPrice().equals(item.getPrice())) {    // 상품 가격이 변동된 경우
                        cart.addMessage(redisProduct.getName() + item.getName() + "의 가격이 변경되었습니다. 확인 부탁드립니다.");
                    }
                    redisItem.setCount(redisItem.getCount() + item.getCount());    // 상품 수량 변경 (추가)
                }
            }
        } else {   // 기존 장바구니에 상품이 존재하지 않는 경우, 새로 추가
            Cart.Product product = Cart.Product.from(form);
            cart.getProducts().add(product);
        }

        // 이미 redisClient 에 고객에 카트가 존재하는 경우, 바뀐 카트를 redisClient 에 덮어쓰기
        // 최초인 경우 redisClient 에 새 카트 추가
        redisClient.put(customerId, cart);
        return cart;
    }
}
