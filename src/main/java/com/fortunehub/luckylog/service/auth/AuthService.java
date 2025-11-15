package com.fortunehub.luckylog.service.auth;

import com.fortunehub.luckylog.common.DbConstraints;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public void signup(SignupRequest request) {
    log.info("[회원가입 시도]");

    try {
      String encodedPassword = passwordEncoder.encode(request.getPassword());
      memberRepository.save(Member.from(request, encodedPassword));

      log.info("[회원 저장 성공]");

    } catch (DataIntegrityViolationException e) {
      // DB의 unique 제약조건 위반 시
      log.error("[회원가입 실패] - [DB 제약조건 위반]", e);
      throw parseConstraintViolation(e);
    }
  }

  private CustomException parseConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMessage().toLowerCase();

    if (message.contains(DbConstraints.UK_MEMBER_EMAIL.toLowerCase())) {
      return new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }
    if (message.contains(DbConstraints.UK_MEMBER_NICKNAME.toLowerCase())) {
      return new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    throw new CustomException(ErrorCode.INVALID_SIGNUP_DATA); // 다른 무결성 제약 위반
  }

  public void login(LoginRequest request) {
    log.info("[로그인 시도]");

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.info("[로그인 성공]");
    } catch (AuthenticationException e) {
      log.warn("[로그인 실패] - [인증 실패]", e);
      throw new CustomException(ErrorCode.LOGIN_FAILED);
    } catch (Exception e) {
      log.error("[로그인 실패] - [시스템 오류]", e);
      throw new CustomException(ErrorCode.LOGIN_SYSTEM_ERROR);
    }
  }
}
