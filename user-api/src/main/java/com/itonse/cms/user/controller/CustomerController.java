package com.itonse.cms.user.controller;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.domain.domain.common.UserVo;
import com.itonse.cms.user.domain.customer.ChangeBalanceForm;
import com.itonse.cms.user.domain.customer.CustomerDto;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.exception.CustomException;
import com.itonse.cms.user.service.customer.CustomerBalanceService;
import com.itonse.cms.user.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.itonse.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")  // 필터 테스트
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())  // 모든 인증을 끝냈기 때문에 findBy~ 할 필요X 바로 findById 로 값 가져와도 됨.
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ChangeBalanceForm form) {
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }
}
