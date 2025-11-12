package com.fortunehub.luckylog.service.auth;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

  public void signup(SignupRequest request) {
    if (memberRepository.existsByEmail(request.getEmail())) {
      log.warn("[AuthService] [회원가입 실패] - [중복 이메일] | email={}", request.getEmail());
      throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    if (request.getNickname() != null && memberRepository.existsByNickname(request.getNickname())) {
      log.warn("[AuthService] [회원가입 실패] - [중복 닉네임] | nickname={}", request.getNickname());
      throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    try {
      String encodedPassword = passwordEncoder.encode(request.getPassword());
      memberRepository.save(Member.from(request, encodedPassword));

      log.info("[AuthService] [회원 저장 성공] | email={} | nickname={}", request.getEmail(),
          request.getNickname());

    } catch (DataIntegrityViolationException e) {
      // DB의 unique 제약조건 위반 시
      log.error("[AuthService] [회원가입 실패] - [DB 제약조건 위반] | email={} | nickname={}",
          request.getEmail(), request.getNickname(), e);
      if (e.getMessage().contains("email")) {
        throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
      } else if (e.getMessage().contains("nickname")) {
        throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
      }
      throw e; // 다른 무결성 제약 위반
    }
  }
}
