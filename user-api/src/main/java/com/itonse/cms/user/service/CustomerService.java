package com.itonse.cms.user.service;

import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id, String email) {
        return customerRepository.findById(id)
                .stream().filter(customer -> customer.getEmail().equals(email))
                .findFirst();
    }

    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmail(email).stream()   // 값이 없으면 Optional 로 리턴
                .filter(customer -> customer.getPassword().equals(password) && customer.isVerify())
                .findFirst();  // 주어진 이메일과 일치하며, 비밀번호가 일치하고 인증된 첫 번째 고객 정보 가져오기.


    }
}
