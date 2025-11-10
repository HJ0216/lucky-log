package com.fortunehub.luckylog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth -> auth.anyRequest().permitAll())
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())) // h2 db 관련 설정
        .httpBasic(httpBasic -> httpBasic.disable()); // HTTP Basic 인증 대신 JWT 사용
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
