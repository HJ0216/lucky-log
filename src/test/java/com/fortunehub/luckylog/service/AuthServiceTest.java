package com.fortunehub.luckylog.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.LoginResponse;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.repository.UserRepository;
import com.fortunehub.luckylog.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthService authService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @Test
  @DisplayName("회원 가입 - 성공")
  void createUser_ValidRequest_ReturnsUserId() {
    // given
    String email = "test@exampl.com";
    String nickname = "test";
    String password = "password123";
    String encodedPassword = "encodedPassword";

    UserCreateRequest request = new UserCreateRequest(email, nickname, password);

    User user = createUser(email, nickname, encodedPassword);

    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when
    UserResponse response = authService.createUser(request);

    // then
    assertThat(response.email()).isEqualTo(request.email());
    assertThat(response.nickname()).isEqualTo(request.nickname());

    verify(passwordEncoder).encode(password);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 사용 가능")
  void isEmailAvailable_NewEmail_ReturnsTrue() {
    // given
    String email = "new@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // when
    boolean available = authService.isEmailAvailable(email);

    // then
    assertThat(available).isTrue();

    verify(userRepository).existsByEmail(email);
  }

  @Test
  @DisplayName("이메일 중복 검사 - 사용 불가능")
  void isEmailAvailable_DuplicateEmail_ReturnsFalse() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // when
    boolean available = authService.isEmailAvailable(email);

    // then
    assertThat(available).isFalse();

    verify(userRepository).existsByEmail(email);
  }

  @Test
  @DisplayName("로그인 - 성공")
  void login_ValidCredentials_ReturnsLoginResponse() {
    // given
    Long userId = 1L;
    String email = "test@example.com";
    String nickname = "test";
    String password = "password123";
    String encodedPassword = "encodedPassword";
    String profileImageUrl = "https://example.com/default.jpg";
    String accessToken = "jwt.access.token";

    User user = createUser(email, nickname, encodedPassword);
    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
    ReflectionTestUtils.setField(user, "profileImageUrl", profileImageUrl);

    LoginRequest loginRequest = new LoginRequest(email, password);

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
    given(jwtUtil.createToken(userId)).willReturn(accessToken);

    // when
    LoginResponse response = authService.login(loginRequest);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.userResponse().id()).isEqualTo(userId);
    assertThat(response.userResponse().email()).isEqualTo(email);
    assertThat(response.userResponse().nickname()).isEqualTo(nickname);
    assertThat(response.userResponse().profileImageUrl()).isEqualTo(profileImageUrl);

    // then: 올바른 파라미터로 메서드가 정말 호출되었는지 확인
    then(userRepository).should().findByEmail(email);
    then(passwordEncoder).should().matches(password, user.getPassword());
    then(jwtUtil).should().createToken(userId);
  }

  @Test
  @DisplayName("로그인 - 존재하지 않는 사용자")
  void login_UserNotFound_ThrowsException() {
    // given
    String email = "notfound@example.com";
    String password = "password123";
    LoginRequest loginRequest = new LoginRequest(email, password);

    given(userRepository.findByEmail(email)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("존재하지 않는 회원입니다.");

    then(userRepository).should().findByEmail(email);
    then(passwordEncoder).should(never()).matches(anyString(), anyString());
    then(jwtUtil).should(never()).createToken(anyLong());
  }

  @Test
  @DisplayName("로그인 - 잘못된 비밀번호")
  void login_WrongPassword_ThrowsException() {
    // given
    String email = "test@example.com";
    String password = "wrongPassword";
    String nickname = "test";
    String encodedPassword = "encodedPassword";

    User user = createUser(email, nickname, encodedPassword);
    LoginRequest loginRequest = new LoginRequest(email, password);

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, encodedPassword);
    verify(jwtUtil, never()).createToken(anyLong());
  }

  private User createUser(String email, String nickname, String password) {
    return new User(email, nickname, password);
  }
}