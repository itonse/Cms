package com.itonse.cms.user.service;

import com.itonse.cms.user.domain.SignUpForm;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.service.customer.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;


@SpringBootTest
class SignUpCustomerServiceTest {   // SignUpCustomerService 서비스 클래스 테스트

    @Autowired  // service 인스턴스에 필요한 의존성 주입 (customerRepository 등)
    private SignUpCustomerService service;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
                .name("name2")
                .birth(LocalDate.now())
                .email("apple10@gmail.com")
                .password("1")
                .phone("01000000000")
                .build();

        Customer c = service.signUp(form);  // 디버깅 할 때 값 확인 가능
        Assert.isTrue(service.signUp(form).getId()!=null);  // 고객 객체가 성공적으로 생성 (식별자인 ID가 할당 됨)
    }
}