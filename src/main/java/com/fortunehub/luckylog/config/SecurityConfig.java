package com.fortunehub.luckylog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth -> auth.requestMatchers("/api/fortune", "/api/v2/fortunes", "/fortune/my", "/fortune/my/**").authenticated()
                        .anyRequest().permitAll())
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())) // h2 db 관련 설정
        .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 대신 JWT 사용
        .securityContext(context -> context
            .requireExplicitSave(false) // 명시적으로 작성하지 않아도 자동으로 SecurityContext에 저장
        )
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        );
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
