package com.itonse.cms.user.controller;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.domain.domain.common.UserVo;
import com.itonse.cms.user.domain.customer.CustomerDto;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.exception.CustomException;
import com.itonse.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.itonse.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    @GetMapping("/getInfo")  // 필터 테스트
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())  // 모든 인증을 끝냈기 때문에 findBy~ 할 필요X 바로 findByid 로 값 가져와도 됨.
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDto.from(c));
    }

}
