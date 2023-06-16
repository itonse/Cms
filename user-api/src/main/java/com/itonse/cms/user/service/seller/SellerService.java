package com.itonse.cms.user.service.seller;

import com.itonse.cms.user.domain.SignUpForm;
import com.itonse.cms.user.domain.model.Seller;
import com.itonse.cms.user.domain.repository.SellerRepository;
import com.itonse.cms.user.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.itonse.cms.user.exception.ErrorCode.*;
import static com.itonse.cms.user.exception.ErrorCode.WRONG_VERIFICATION;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public Optional<Seller> findByIdAndEmail(Long id, String email) {
        return sellerRepository.findByIdAndEmail(id, email);
    }

    public Optional<Seller> findValidSeller(String email, String password) {
        return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
    }

    public Seller signUp(SignUpForm form) {
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isExistEmail(String email) {
        return sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();
    }

    @Transactional
    public void changeSellerValidateEmail(Long sellerId, String verificationCode) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        seller.setVerificationExpireAt(LocalDateTime.now().plusDays(1));
        seller.setVerificationCode(verificationCode);
    }

    @Transactional
    public void verifySellerEmail(String email, String code) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (seller.isVerify()) {
            throw new CustomException(ALREADY_VERIFY);
        } else if (seller.getVerificationExpireAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(EXPIRE_CODE);
        } else if (!seller.getVerificationCode().equals(code)) {
            throw new CustomException(WRONG_VERIFICATION);
        }

        seller.setVerify(true);
    }
}
