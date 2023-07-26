package com.itonse.cms.user.controller;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.domain.domain.common.UserVo;
import com.itonse.cms.user.domain.Seller.SellerDto;
import com.itonse.cms.user.domain.customer.ChangeBalanceForm;
import com.itonse.cms.user.domain.customer.CustomerDto;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.domain.model.Seller;
import com.itonse.cms.user.exception.CustomException;
import com.itonse.cms.user.service.customer.CustomerBalanceService;
import com.itonse.cms.user.service.customer.CustomerService;
import com.itonse.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.itonse.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final JwtAuthenticationProvider provider;
    private final SellerService sellerService;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Seller s = sellerService.findByIdAndEmail(vo.getId(), vo.getEmail())  // 모든 인증을 끝냈기 때문에 findBy~ 할 필요X 바로 findById 로 값 가져와도 됨.
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(SellerDto.from(s));
    }
}
