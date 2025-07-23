package com.fortunehub.luckylog.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.response.LoginResponse;
import com.fortunehub.luckylog.repository.UserRepository;
import com.fortunehub.luckylog.util.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
  @DisplayName("로그인 - 성공")
  void login_ValidCredentials_ReturnsLoginResponse() {
    // given
    String email = "test@exampl.com";
    String nickname = "test";
    String password = "password123";
    String encodedPassword = "encodedPassword";
    String accessToken = "jwt.access.token";

    User user = createUser(email, nickname, encodedPassword);
    LoginRequest loginRequest = new LoginRequest(email, password);

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
    given(jwtUtil.createToken(email)).willReturn(accessToken);

    // when
    LoginResponse response = authService.login(loginRequest);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.userResponse().email()).isEqualTo(email);
    assertThat(response.userResponse().nickname()).isEqualTo("test");

    // verify: 올바른 파라미터로 메서드가 정말 호출되었는지 확인
    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, encodedPassword);
    verify(jwtUtil).createToken(email);
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

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder, never()).matches(anyString(), anyString()); // 호출되지 않았어야 함
    verify(jwtUtil, never()).createToken(anyString());
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
    verify(jwtUtil, never()).createToken(anyString());
  }

  private User createUser(String email, String nickname, String password) {
    return new User(email, nickname, password);
  }
}