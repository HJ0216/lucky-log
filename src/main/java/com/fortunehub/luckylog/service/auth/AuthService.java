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

  public Member login(LoginRequest request) {
    String email = request.getEmail();
    log.info("[로그인 시도] email={}", email);

    Member member = memberRepository.findByEmail(email)
                                    .orElseThrow(() -> {
                                      log.warn("[로그인 실패] - [계정 없음] email={}", email);
                                      return new CustomException(ErrorCode.LOGIN_FAILED);
                                    });

    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
      log.warn("[로그인 실패] - [비밀번호 불일치] email={}", email);
      throw new CustomException(ErrorCode.LOGIN_FAILED);
    }

    log.info("[로그인 성공] memberId={}", member.getId());

    return member;
  }
}
