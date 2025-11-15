package com.fortunehub.luckylog.service.auth;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public void signup(SignupRequest request) {
    if (memberRepository.existsByEmail(request.getEmail())) {
      log.warn("[회원가입 실패] - [중복 이메일]");
      throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    if (request.getNickname() != null && memberRepository.existsByNickname(request.getNickname())) {
      log.warn("[회원가입 실패] - [중복 닉네임]");
      throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    try {
      String encodedPassword = passwordEncoder.encode(request.getPassword());
      memberRepository.save(Member.from(request, encodedPassword));

      log.info("[회원 저장 성공]");

    } catch (DataIntegrityViolationException e) {
      // DB의 unique 제약조건 위반 시
      log.error("[회원가입 실패] - [DB 제약조건 위반]", e);
      if (e.getMessage().contains("email")) {
        throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
      } else if (e.getMessage().contains("nickname")) {
        throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
      }
      throw e; // 다른 무결성 제약 위반
    }
  }

  @Transactional(readOnly = true)
  public void login(LoginRequest request) {
    log.info("[로그인 시도] email={}", request.getEmail());

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.info("[로그인 성공] email={}", request.getEmail());
    } catch (BadCredentialsException e) {
      log.warn("[로그인 실패] - [인증 실패] email={}", request.getEmail());
      throw new CustomException(ErrorCode.LOGIN_FAILED);
    }
  }
}
