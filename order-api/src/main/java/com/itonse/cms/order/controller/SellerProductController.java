package com.itonse.cms.order.controller;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.order.domain.product.AddProductForm;
import com.itonse.cms.order.domain.product.ProductDto;
import com.itonse.cms.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final JwtAuthenticationProvider provider;  //토큰 발행

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody AddProductForm form) {
        return ResponseEntity.ok(ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form)));
    }
}
