package com.fortunehub.luckylog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
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

  // ========== 이메일 중복 검사 API 테스트 ==========
  @Test
  @DisplayName("이메일 중복 검사 - 사용 가능")
  void checkEmailDuplicate_AvailableEmail_ReturnsOk() throws Exception {
    // given
    String email = "new@email.com";

    // when & then
    mockMvc.perform((get(BASE_URL + "/check-email")
               .param("email", email)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.available").value(true))
           .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 중복 이메일")
  void checkEmailDuplicate_DuplicateEmail_ReturnsConflict() throws Exception {
    // given
    String email = "duplicate@email.com";
    User user = new User(email, "user", "password123");
    userRepository.save(user);

    // when & then
    mockMvc.perform(get(BASE_URL + "/check-email")
               .param("email", email))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.available").value(false))
           .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 파라미터 누락")
  void checkEmailDuplicate_MissingEmail_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/check-email"))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("MISSING_REQUIRED_PARAMETER"))
           .andExpect(jsonPath("$.message").value(containsString("필수 파라미터(email)가 누락되었습니다.")));
  }


  // ========== 회원 가입 API 테스트 ==========
  @Test
  @DisplayName("회원 가입 - 성공")
  void createUser_ValidRequest_ReturnsCreated() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "test",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isCreated());

    // 실제 DB에 저장되었는지 확인
    User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    assertThat(savedUser.getNickname()).isEqualTo("test");
  }

  @Test
  @DisplayName("회원 가입 - 이메일 필드 누락")
  void createUser_EmptyEmail_ReturnsBadRequest() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "",
        "test",
        "password123"
    );

    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 필수입니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 잘못된 이메일 형식")
  void createUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "invalid-email", // 잘못된 이메일 형식
        "testuser",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("올바른 이메일 형식이 아닙니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 이메일 길이 초과")
  void createUser_EmailTooLong_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "verylongemailaddressthatexceedsfiftycharacters@example.com", // 50자 초과
        "testuser",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 50자를 초과할 수 없습니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 필드 누락")
  void createUser_EmptyNickname_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "", // 빈 닉네임
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 필수입니다.")))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 길이 부족")
  void createUser_NicknameTooShort_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "a", // 1자 (2자 미만)
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 길이 초과")
  void createUser_NicknameTooLong_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "verylongnickname", // 8자 초과
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 비밀번호 필드 누락")
  void createUser_EmptyPassword_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "testuser",
        ""
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 필수입니다.")))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 비밀번호 길이 부족")
  void createUser_PasswordTooShort_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "testuser",
        "1234567" // 7자 (8자 미만)
    );

    // when & then
    mockMvc.perform(post(BASE_URL + "/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value("password: 비밀번호는 8자 이상 20자 이하여야 합니다."));
  }

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