package com.itonse.cms.user.application;

import com.itonse.cms.user.client.MailgunClient;
import com.itonse.cms.user.client.mailgun.SendMailForm;
import com.itonse.cms.user.domain.SignUpForm;
import com.itonse.cms.user.domain.model.Customer;
import com.itonse.cms.user.domain.model.Seller;
import com.itonse.cms.user.exception.CustomException;
import com.itonse.cms.user.service.customer.SignUpCustomerService;
import com.itonse.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import static com.itonse.cms.user.exception.ErrorCode.ALREADY_REGISTERED_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SellerService sellerService;

    // 고객 회원가입 & 이메일 인증
    public String customerSignUp(SignUpForm form) {   // 회원가입 진행
        if (signUpCustomerService.isEmailExist(form.getEmail())) {  // 이미 가입된 이메일이면 예외발생
            throw new CustomException(ALREADY_REGISTERED_USER);  // 커스텀 익셉션으로 예외처리
        } else {   // 인증 메일 보내기
            Customer c = signUpCustomerService.signUp(form);
            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                            .from("tester@dannymytester.com")
                            .to(form.getEmail())
                            .subject("Verification Email!")
                            .text(getVerificationEmailBody(c.getEmail(), c.getName(), "customer", code ))
                                    .build();

            log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());  // 메일 보내고 결과 로그 찍기

            signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);  // 이메일에 대한 validation 상태 변경

            return "회원 가입에 성공하였습니다.";
        }
    }

    public void customerVerify(String email, String code) {   // 이메일 인증
        signUpCustomerService.verifyCustomerEmail(email, code);
    }


    // 판매자 회원가입 & 이메일 인증
    public String sellerSignUp(SignUpForm form) {
        if (sellerService.isExistEmail(form.getEmail())) {  // 이미 가입된 회원 이메일인지 확인
            throw new CustomException(ALREADY_REGISTERED_USER);
        } else {  // 인증 메일 보내기

            Seller s = sellerService.signUp(form);
            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("sellertester@dannymytester.com")
                    .to(form.getEmail())
                    .subject("New Seller Verification Email :)")
                    .text(getVerificationEmailBody(s.getEmail(), s.getName(), "seller", code))
                        .build();

            log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());  // 메일 보내고 결과 로그 찍기

            sellerService.changeSellerValidateEmail(s.getId(), code);

            return "회원 가입에 성공하였습니다.";
        }
    }

    public void sellerVerify(String email, String code) {
        sellerService.verifySellerEmail(email, code);
    }


    // 이메일 전송 유틸 ( 랜덤 코드 생성, 전송 메일 양식 )

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name, String type, String code) {  // 이메일 내용 (링크를 클릭하면 검증 완료)
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name).append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/signup/" + type + "/verify?email=")
                .append(email)
                .append("&code=")
                .append(code)  // 위에서 생성한 코드가 들어감
                .toString();
    }
}
