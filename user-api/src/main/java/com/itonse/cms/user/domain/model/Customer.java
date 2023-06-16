package com.itonse.cms.user.domain.model;

import com.itonse.cms.user.domain.SignUpForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Builder
@AuditOverride(forClass = BaseEntity.class)   // 베이스엔티티 클래스 상속받음
public class Customer extends BaseEntity {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Customer 의 PK

    @Column(unique = true)  // 유니크 컬럼
    private String email;
    private String name;
    private String password;
    private String phone;;
    private LocalDate birth;   // "yyyy-MM-dd"

    private LocalDateTime verifyExpiredAt;   // 인증 만료 기한 (1일)
    private String verificationCode;   // 회원가입시 인증코드
    private boolean verify;   // 회원가입시 이메일 인증 여부

    public static Customer from(SignUpForm form) {
        return Customer.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))   // 유니크 제약 조건을 지키키: 대소문자 구분하지 않는 검색과 비교를 수행
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone())
                .verify(false)
                .build();
    }
}
