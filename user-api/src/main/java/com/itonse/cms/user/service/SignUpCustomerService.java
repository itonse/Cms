package com.itonse.cms.user.service;

import com.itonse.cms.user.domain.SignUpForm;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // 의존성 주입 시 번거로운 작업을 줄임
public class SignUpCustomerService {  // 고객 회원가입 서비스

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));   // 커스터머 객체 생성하여 저장
    }

}
