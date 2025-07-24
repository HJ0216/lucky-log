package com.fortunehub.luckylog.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

  private static final String BASE_URL = "/api/v1/auth";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // ========== 로그인 API 테스트 ==========
  @Test
  @DisplayName("로그인 - 성공")
  void login_ValidCredentials_ReturnsOk() throws Exception {
    // given
    String email = "test@example.com";
    String password = "password123";
    String nickname = "test";

    // 사용자 미리 생성 (실제 암호화된 비밀번호로)
    User user = new User(email, nickname, passwordEncoder.encode(password));
    userRepository.save(user);

    LoginRequest request = new LoginRequest(email, password);

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.accessToken").exists())
           .andExpect(jsonPath("$.tokenType").value("Bearer"))
           .andExpect(jsonPath("$.userResponse.email").value(email))
           .andExpect(jsonPath("$.userResponse.nickname").value(nickname));
  }

  @Test
  @DisplayName("로그인 - 존재하지 않는 사용자")
  void login_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest("notfound@example.com", "password123");

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("로그인 - 잘못된 비밀번호")
  void login_WrongPassword_ReturnsBadRequest() throws Exception {
    // given
    String email = "test@example.com";
    String correctPassword = "password123";
    String wrongPassword = "wrongpassword";
    String nickname = "test";

    // 사용자 미리 생성
    User user = new User(email, nickname, passwordEncoder.encode(correctPassword));
    userRepository.save(user);

    LoginRequest request = new LoginRequest(email, wrongPassword);

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
  }

  @Test
  @DisplayName("로그인 - 이메일 필드 누락")
  void login_EmptyEmail_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest("", "password123");

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 필수입니다.")));
  }

  @Test
  @DisplayName("로그인 - 잘못된 이메일 형식")
  void login_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest("invalid-email", "password123");

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("올바른 이메일 형식이 아닙니다.")));
  }

  @Test
  @DisplayName("로그인 - 이메일 길이 초과")
  void login_EmailTooLong_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest(
        "verylongemailaddressthatexceedsfiftycharacters@example.com", // 50자 초과
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 50자를 초과할 수 없습니다.")));
  }

  @Test
  @DisplayName("로그인 - 비밀번호 필드 누락")
  void login_EmptyPassword_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest("test@example.com", "");

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 필수입니다.")))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("로그인 - 비밀번호 길이 부족")
  void login_PasswordTooShort_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest("test@example.com", "1234567"); // 7자

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("로그인 - 비밀번호 길이 초과")
  void login_PasswordTooLong_ReturnsBadRequest() throws Exception {
    // given
    LoginRequest request = new LoginRequest(
        "test@example.com",
        "verylongpasswordthatexceedstwentycharacters" // 20자 초과
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("로그인 - RequestBody 누락")
  void login_MissingRequestBody_ReturnsBadRequest() throws Exception {
    // when & then
    mockMvc.perform(post(BASE_URL + "/login")
               .contentType(MediaType.APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
           .andExpect(jsonPath("$.message").value("요청 본문이 누락되었거나 형식이 올바르지 않습니다."));
  }
}