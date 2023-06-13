package com.itonse.cms.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)  // 2.0 문서버전 지정
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itonse.cms"))  // basePackage 기준으로 많이 함.
                .paths(PathSelectors.any())   // any(): DiaryController 에 있는 모든 API (n개)가 나오게 하겠다.
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()   // apiInfo 를 빌드하는 객체
                .title("user-api ~")
                .description("null")
                .version("2.0")
                .build();
    }
}
