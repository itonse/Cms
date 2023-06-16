package com.itonse.cms.user.controller;

import com.itonse.cms.user.application.SignUpApplication;
import com.itonse.cms.user.domain.SignUpForm;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api("sign-up-controller")
@RestController
@RequestMapping("/signup")   // 공통 경로
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    // 고객 회원가입
    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpApplication.customerSignUp(form));
    }

    // 고객 이메일 인증
    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        signUpApplication.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");

    }

    // 판매자 회원가입
    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpApplication.sellerSignUp(form));
    }

    // 판매자 이메일 인증
    @GetMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(String email, String code) {
        signUpApplication.sellerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
