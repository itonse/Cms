package com.itonse.cms.user.application;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.domain.domain.common.UserType;
import com.itonse.cms.user.domain.SignInForm;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.exception.CustomException;
import com.itonse.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.itonse.cms.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        // 1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        // 2. 토큰을 발행하고
        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);

        // 3. 토큰을 response 한다.

    }
}
