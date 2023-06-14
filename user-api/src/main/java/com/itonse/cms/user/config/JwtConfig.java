package com.itonse.cms.user.config;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {   // 편리함을 위해 빈으로 등록
        return new JwtAuthenticationProvider();
    }
}
