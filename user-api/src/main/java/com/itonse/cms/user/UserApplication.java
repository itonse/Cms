package com.itonse.cms.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.itonse.cms")   // 멀티모듈에서 db에 테이블이 생성되게 하기 위해 필요
@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing   // 베이스 엔티티의 @CreatedDate 와 @LastModifiedDate 기능 활성화
@EnableJpaRepositories   // Spring Data JPA 레파지토리 기능 활성화
@RequiredArgsConstructor
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
