package com.itonse.cms.user.service.customer;

import com.itonse.cms.user.domain.SignUpForm;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.domain.repository.CustomerRepository;
import com.itonse.cms.user.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.itonse.cms.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor  // 의존성 주입 시 번거로운 작업을 줄임
public class SignUpCustomerService {  // 고객 회원가입 서비스

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {  // 회원가입 진행
        return customerRepository.save(Customer.from(form));   // 커스터머 객체 생성하여 저장
    }

    public boolean isEmailExist(String email) {   // 가입된 이메일 정보인지 확인
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();   // 이미 해당 이메일이 존재하는지 여부를(true,false) 반환
    }

    @Transactional
    public LocalDateTime changeCustomerValidateEmail(Long customerId, String verificationCode) {  // 이메일에 대한 validation 상태 변경
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));  // 인증 만료일

            return customer.getVerifyExpiredAt();
        }
        throw new CustomException(NOT_FOUND_USER);
    }

    @Transactional
    public void verifyCustomerEmail(String email, String code) {   // 이메일 인증
        Customer customer = customerRepository.findByEmail(email)   // 이메일에 해당하는 회원 정보 가져오기
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if(customer.isVerify()) {   // 이미 인증된 경우
            throw new CustomException(ALREADY_VERIFY);
        } else if(!customer.getVerificationCode().equals(code)) {  // 코드가 다른 경우
            throw new CustomException(WRONG_VERIFICATION);
        } else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {  // 인증 만료기한이 지난 경우
            throw new CustomException(EXPIRE_CODE);
        }

        customer.setVerify(true);   // 인증완료 처리
    }
}
