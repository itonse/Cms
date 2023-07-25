package com.itonse.cms.order.controller;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.order.application.CartApplication;
import com.itonse.cms.order.domain.product.AddProductCartForm;
import com.itonse.cms.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/customer/cart")
@RequiredArgsConstructor
@RestController
public class CustomerCartController {

    private final CartApplication cartApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form) {
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }
}
