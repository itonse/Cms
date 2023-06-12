package com.itonse.cms.user.domain.model;

import com.itonse.cms.user.domain.SignUpForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Entity
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

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    public static Customer from(SignUpForm form) {
        return Customer.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))  // 유니크값 검사
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone())
                .verify(false)
                .build();
    }
}
