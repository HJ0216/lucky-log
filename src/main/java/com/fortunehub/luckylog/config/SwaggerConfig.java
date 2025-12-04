package com.fortunehub.luckylog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    String securitySchemeName = "cookieAuth";

    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes(securitySchemeName, securityScheme())) // 보안 방식 정의
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 전역 보안 적용
        .info(apiInfo()); // API 기본 정보
  }

  // Session 기반 인증 (Cookie)
  private SecurityScheme securityScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY) // Cookie를 표현하려면 APIKEY 타입을 써야 함
        .in(SecurityScheme.In.COOKIE) // 위치: 쿠키에 있음
        .name("JSESSIONID"); // Spring Security의 기본 세션 쿠키 이름
  }

  private Info apiInfo() {
    return new Info()
        .title("Lucky Log API")
        .description("Lucky Log 운세 서비스 REST API 문서")
        .version("2.0.0")
        .contact(new Contact()
            .name("HJ0216")
            .email("6120hj@gmail.com"));

  }
}
