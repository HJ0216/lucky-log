package com.fortunehub.luckylog.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class) // Mockito 관련 애노테이션(@Mock, @InjectMocks 등)을 자동으로 초기화
@DisplayName("회원가입 서비스")
class AuthServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks // Mockito가 AuthService 객체 생성 + mock 의존성 주입
  private AuthService authService;

  private static final String TEST_EMAIL = "lucky@email.com";
  private static final String TEST_RAW_PASSWORD = "password147@";
  private static final String TEST_ENCODED_PASSWORD = "encodedPassword123";
  private static final String TEST_NICKNAME = "솜사탕";

  @Test
  @DisplayName("올바른 회원 정보로 회원가입하면 회원이 저장된다")
  void signup_Success() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(false);
    given(passwordEncoder.encode(req.getPassword())).willReturn(TEST_ENCODED_PASSWORD);

    // when
    authService.signup(req);

    // then
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(captor.capture());

    Member savedMember = captor.getValue();
    assertThat(savedMember.getEmail()).isEqualTo(req.getEmail());
    assertThat(savedMember.getPassword())
        .isEqualTo(TEST_ENCODED_PASSWORD)
        .isNotEqualTo(TEST_RAW_PASSWORD);
    assertThat(savedMember.getNickname()).isEqualTo(req.getNickname());
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 회원가입하면 예외가 발생한다")
  void signup_DuplicateEmail() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, "솜사탕");

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage())
        .extracting("errorCode")
        .isEqualTo(ErrorCode.DUPLICATE_EMAIL);

    verify(memberRepository).existsByEmail(req.getEmail());
  }

  @Test
  @DisplayName("이미 존재하는 닉네임으로 회원가입하면 예외가 발생한다")
  void signup_DuplicateNickname() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.DUPLICATE_NICKNAME.getMessage())
        .extracting("errorCode")
        .isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

    verify(memberRepository).existsByEmail(req.getEmail());
    verify(memberRepository).existsByNickname(req.getNickname());
  }

  @DisplayName("동시에 이메일과 닉네임이 중복되면 이메일 중복 예외가 먼저 발생한다")
  void signup_BothDuplicate_EmailFirst() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);
    given(memberRepository.existsByEmail(req.getEmail())).willReturn(true);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.DUPLICATE_EMAIL);

    verify(memberRepository).existsByEmail(req.getEmail());
  }

  @Test
  @DisplayName("닉네임이 null이면 닉네임 중복 검사를 하지 않고 회원가입에 성공한다")
  void signup_WithoutNickname() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, null);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(passwordEncoder.encode(req.getPassword())).willReturn(TEST_ENCODED_PASSWORD);

    // when
    authService.signup(req);

    // then
    verify(memberRepository).existsByEmail(req.getEmail());
    verify(passwordEncoder).encode(TEST_RAW_PASSWORD);

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(captor.capture());

    Member savedMember = captor.getValue();
    assertThat(savedMember.getEmail()).isEqualTo(req.getEmail());
    assertThat(savedMember.getPassword())
        .isEqualTo(TEST_ENCODED_PASSWORD)
        .isNotEqualTo(TEST_RAW_PASSWORD);
    assertThat(savedMember.getNickname()).isNull();
  }

  @Test
  @DisplayName("저장 시 이메일 DB 제약조건 위반이 발생하면 중복 이메일 예외가 발생한다")
  void signup_DataIntegrityViolation_Email() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(false);
    given(passwordEncoder.encode(req.getPassword())).willReturn(TEST_ENCODED_PASSWORD);
    given(memberRepository.save(any(Member.class)))
        .willThrow(new DataIntegrityViolationException("Duplicate entry for email"));

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage())
        .extracting("errorCode")
        .isEqualTo(ErrorCode.DUPLICATE_EMAIL);

    verify(memberRepository).existsByEmail(req.getEmail());
    verify(memberRepository).existsByNickname(req.getNickname());
    verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
    verify(memberRepository).save(any(Member.class));
  }

  @Test
  @DisplayName("저장 시 닉네임 DB 제약조건 위반이 발생하면 중복 닉네임 예외가 발생한다")
  void signup_DataIntegrityViolation_Nickname() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(false);
    given(passwordEncoder.encode(req.getPassword())).willReturn(TEST_ENCODED_PASSWORD);
    given(memberRepository.save(any(Member.class)))
        .willThrow(new DataIntegrityViolationException("Duplicate entry for nickname"));

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.DUPLICATE_NICKNAME.getMessage())
        .extracting("errorCode")
        .isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

    verify(memberRepository).existsByEmail(req.getEmail());
    verify(memberRepository).existsByNickname(req.getNickname());
    verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
    verify(memberRepository).save(any(Member.class));
  }

  @Test
  @DisplayName("저장 시 다른 DB 제약조건 위반이 발생하면 원본 예외가 발생한다")
  void signup_DataIntegrityViolation_Other() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

    given(memberRepository.existsByEmail(req.getEmail())).willReturn(false);
    given(memberRepository.existsByNickname(req.getNickname())).willReturn(false);
    given(passwordEncoder.encode(req.getPassword())).willReturn(TEST_ENCODED_PASSWORD);

    DataIntegrityViolationException exception =
        new DataIntegrityViolationException("Other constraint violation");
    given(memberRepository.save(any(Member.class))).willThrow(exception);

    // when & then
    assertThatThrownBy(() -> authService.signup(req))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessage("Other constraint violation");

    verify(memberRepository).existsByEmail(req.getEmail());
    verify(memberRepository).existsByNickname(req.getNickname());
    verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
    verify(memberRepository).save(any(Member.class));
  }
}