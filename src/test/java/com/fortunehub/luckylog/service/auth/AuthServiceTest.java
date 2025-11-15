package com.fortunehub.luckylog.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import com.fortunehub.luckylog.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class) // Mockito 관련 애노테이션(@Mock, @InjectMocks 등)을 자동으로 초기화
@DisplayName("회원 Service")
class AuthServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks // Mockito가 AuthService 객체 생성 + mock 의존성 주입
  private AuthService authService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext(); // 테스트 간 격리를 위해 초기화
  }

  private static final String TEST_EMAIL = "lucky@email.com";
  private static final String TEST_NOT_FOUND_EMAIL = "unlucky@email.com";
  private static final String TEST_RAW_PASSWORD = "password147@";
  private static final String TEST_ENCODED_PASSWORD = "encodedPassword123";
  private static final String TEST_NICKNAME = "솜사탕";

  @Test
  @DisplayName("정상적인 회원가입 요청 시 회원이 저장된다")
  void signup_WhenValidRequest_ThenSavesMember() {
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
  @DisplayName("중복된 이메일로 회원가입 시 예외가 발생한다")
  void signup_WhenEmailDuplicated_ThenThrowsException() {
    // given
    SignupRequest req = new SignupRequest(TEST_EMAIL, TEST_RAW_PASSWORD, TEST_NICKNAME);

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
  @DisplayName("중복된 닉네임으로 회원가입 시 예외가 발생한다")
  void signup_WhenNicknameDuplicated_ThenThrowsException() {
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

  @Test
  @DisplayName("닉네임 없이 회원가입 시 정상 처리된다")
  void signup_WhenNicknameNull_ThenSavesSuccessfully() {
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
  @DisplayName("이메일 중복 제약조건 위반 시 예외가 발생한다")
  void signup_WhenEmailConstraintViolated_ThenThrowsDuplicateEmailException() {
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
  @DisplayName("닉네임 중복 제약조건 위반 시 예외가 발생한다")
  void signup_WhenNicknameConstraintViolated_ThenThrowsDuplicateNicknameException() {
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
  @DisplayName("기타 제약조건 위반 시 예외가 발생한다")
  void signup_WhenOtherConstraintViolated_ThenThrowsException() {
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


  @Test
  @DisplayName("정상적인 로그인 요청 시 로그인에 성공한다")
  void login_WhenValidCredentials_ThenSuccess() {
    // given
    LoginRequest req = new LoginRequest(TEST_EMAIL, TEST_RAW_PASSWORD);
    Member member = new Member(TEST_EMAIL, TEST_ENCODED_PASSWORD, TEST_NICKNAME);

    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
    );

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willReturn(authentication);

    // when
    authService.login(req);

    // then
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(currentAuth).isNotNull();
    assertThat(currentAuth.getPrincipal()).isInstanceOf(CustomUserDetails.class);

    CustomUserDetails principal = (CustomUserDetails) currentAuth.getPrincipal();
    assertThat(principal.getUsername()).isEqualTo(req.getEmail());
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  void login_WhenEmailNotFound_ThenThrowsException() {
    // given
    LoginRequest req = new LoginRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willThrow(new BadCredentialsException("User not found"));

    // when & then
    assertThatThrownBy(() -> authService.login(req))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  void login_WhenInvalidPassword_ThenThrowsException() {
    // given
    LoginRequest req = new LoginRequest(TEST_EMAIL, "Wrong147@");

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willThrow(new BadCredentialsException("Bad credentials"));

    // when & then
    assertThatThrownBy(() -> authService.login(req))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }
}