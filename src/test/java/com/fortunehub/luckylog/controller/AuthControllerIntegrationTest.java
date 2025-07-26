package com.fortunehub.luckylog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthController 클래스")
class AuthControllerIntegrationTest {

  private static final String BASE_URL = "/api/v1/auth";
  private final String email = "test@email.com";
  private final String nickname = "test";
  private final String password = "password123";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Nested
  @DisplayName("checkEmailDuplicate 메서드는")
  class Describe_checkEmailDuplicate{
    private static final String UNUSED_EMAIL = "unused@email.com";
    private static final String EXISTING_EMAIL = "existing@email.com";

    @Nested
    @DisplayName("만약 가입하지 않은 이메일이면")
    class Context_with_unused_email{
      @Test
      @DisplayName("사용 가능하다는 응답을 반환한다")
      void it_returns_email_available() throws Exception {
        mockMvc.perform((get(BASE_URL + "/check-email")
                   .param("email", UNUSED_EMAIL)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.available").value(true))
               .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));
      }
    }
    
    @Nested
    @DisplayName("만약 이미 가입한 이메일이면")
    class Context_with_used_email{
      @Test
      @DisplayName("사용 불가능하다는 응답을 반환한다")
      void it_returns_duplicate_email_error() throws Exception {
        User user = createUser(EXISTING_EMAIL, nickname, password);
        userRepository.save(user);

        mockMvc.perform(get(BASE_URL + "/check-email")
                   .param("email", EXISTING_EMAIL))
               .andDo(print())
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.available").value(false))
               .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
      }
    }

    @Nested
    @DisplayName("만약 이메일이 누락되면")
    class Context_with_email_parameter_missing{
      @Test
      @DisplayName("이메일이 누락되었다는 응답을 반환한다")
      void it_returns_missing_email_error() throws Exception {
        mockMvc.perform(get(BASE_URL + "/check-email"))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("MISSING_REQUIRED_PARAMETER"))
               .andExpect(jsonPath("$.message").value(containsString("필수 파라미터(email)가 누락되었습니다.")));
      }
    }
  }

  private User createUser(String email, String nickname, String password) {
    return new User(email, nickname, password);
  }

  @Nested
  @DisplayName("createUser 메서드는")
  class Describe_createUser{
    private static final String VALID_EMAIL = "valid@email.com";
    private static final String VALID_NICKNAME = "johndoe";
    private static final String VALID_PASSWORD = "password123";

    @Nested
    @DisplayName("만약 유효한 가입 요청이면")
    class Context_with_valid_sign_up_request{
      @Test
      @DisplayName("회원가입에 성공한다")
      void it_returns_sign_up_success_response() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(VALID_EMAIL, VALID_NICKNAME, VALID_PASSWORD);

        // when & then
        MvcResult result = mockMvc.perform(post(BASE_URL + "/signup")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request)))
                                     .andDo(print())
                                     .andExpect(status().isCreated())
                                     .andExpect(jsonPath("$.id").exists())
                                     .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                                     .andExpect(jsonPath("$.nickname").value(VALID_NICKNAME))
                                     .andExpect(jsonPath("$.password").doesNotExist())
                                     .andReturn();

        // Location 헤더 검증
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long createdUserId = jsonNode.get("id").asLong();

        mockMvc.perform(get(result.getResponse().getHeader("Location")))
               .andExpect(status().isOk());

        assertThat(result.getResponse().getHeader("Location"))
            .isEqualTo("/api/v1/users/" + createdUserId);

        // 실제 DB에 저장되었는지 확인
        User savedUser = userRepository.findByEmail(request.email()).orElseThrow();
        assertThat(savedUser.getId()).isEqualTo(createdUserId);
        assertThat(savedUser.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(savedUser.getNickname()).isEqualTo(VALID_NICKNAME);
        assertThat(savedUser.getPassword()).isNotEqualTo(VALID_PASSWORD);
      }
    }

    @Nested
    @DisplayName("만약 이메일 검증에 실패하면")
    class Context_with_invalid_email{
      @Test
      @DisplayName("이메일이 누락되었다는 응답을 반환한다")
      void it_returns_missing_email_error() throws Exception {
        UserCreateRequest request = new UserCreateRequest("", nickname, password);

        mockMvc.perform(post(BASE_URL + "/signup")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value(containsString("이메일은 필수입니다.")));
      }

      @Test
      @DisplayName("이메일 형식이 잘못되었다는 응답을 반환한다")
      void it_returns_invalid_email_format_error() throws Exception {
        // given
        String invalidEmail = "invalid-email";
        UserCreateRequest request = new UserCreateRequest(invalidEmail, nickname, password);

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
      @DisplayName("이메일이 너무 길다는 응답을 반환한다")
      void it_returns_email_too_long_error() throws Exception {
        // given
        String tooLongEmail = "verylongemailaddressthatexceedsfiftycharacters@example.com";
        UserCreateRequest request = new UserCreateRequest(tooLongEmail, nickname, password);

        // when & then
        mockMvc.perform(post(BASE_URL + "/signup")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value(containsString("이메일은 50자를 초과할 수 없습니다.")));
      }
    }

    @Nested
    @DisplayName("만약 닉네임 검증에 실패하면")
    class Context_with_invalid_nickname{
      @Test
      @DisplayName("닉네임이 누락되었다는 응답을 반환한다")
      void it_returns_missing_nickname_error() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(email, "", password);

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
      @DisplayName("닉네임이 너무 짧다는 응답을 반환한다")
      void it_returns_nickname_too_short_error() throws Exception {
        // given
        String tooShortNickname = "a";
        UserCreateRequest request = new UserCreateRequest(email, tooShortNickname, password);

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
      @DisplayName("닉네임이 너무 길다는 응답을 반환한다")
      void it_returns_nickname_too_long_error() throws Exception {
        // given
        String tooLongNickname = "verylongnickname";
        UserCreateRequest request = new UserCreateRequest(email, tooLongNickname, password);

        // when & then
        mockMvc.perform(post(BASE_URL + "/signup")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
      }

    }

    @Nested
    @DisplayName("만약 비밀번호 검증에 실패하면")
    class Context_with_invalid_password{
      @Test
      @DisplayName("비밀번호가 누락되었다는 응답을 반환한다")
      void it_returns_missing_password_error() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(email, nickname, "");

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
      @DisplayName("비밀번호가 너무 짧다는 응답을 반환한다")
      void it_returns_password_too_short_error() throws Exception {
        // given
        String tooShortPassword = "1234567";

        UserCreateRequest request = new UserCreateRequest(email, nickname, tooShortPassword);

        // when & then
        mockMvc.perform(post(BASE_URL + "/signup")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value("password: 비밀번호는 8자 이상 20자 이하여야 합니다."));
      }
    }

  }

  @Nested
  @DisplayName("login 메서드는")
  class Describe_login {

    @Nested
    @DisplayName("만약 유효한 로그인 요청이면")
    class Context_with_valid_login_request {

      @Test
      @DisplayName("로그인에 성공한다")
      void it_returns_login_success_response() throws Exception {
        // given
        // 사용자 미리 생성 (실제 암호화된 비밀번호로)
        User user = createUser(email, nickname, passwordEncoder.encode(password));
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
               .andExpect(jsonPath("$.userResponse.id").exists())
               .andExpect(jsonPath("$.userResponse.email").value(email))
               .andExpect(jsonPath("$.userResponse.nickname").value(nickname))
               .andExpect(jsonPath("$.userResponse.profileImageUrl").isEmpty())
               .andExpect(jsonPath("$.userResponse.createdAt").isNotEmpty());
      }
    }

    @Nested
    @DisplayName("만약 유효하지 않은 이메일로 로그인 요청을 하면")
    class Context_with_invalid_email {

      @Test
      @DisplayName("이메일이 누락되었다는 응답을 반환한다")
      void it_returns_missing_email_error() throws Exception {
        // given
        LoginRequest request = new LoginRequest("", password);

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
      @DisplayName("이메일 형식이 잘못되었다는 응답을 반환한다")
      void it_returns_invalid_email_format_error() throws Exception {
        // given
        String invalidEmail = "invalid-email";
        LoginRequest request = new LoginRequest(invalidEmail, password);

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
      @DisplayName("이메일이 너무 길다는 응답을 반환한다")
      void it_returns_email_too_long_error() throws Exception {
        // given
        String tooLongEmail = "verylongemailaddressthatexceedsfiftycharacters@example.com";
        LoginRequest request = new LoginRequest(tooLongEmail, password);

        // when & then
        mockMvc.perform(post(BASE_URL + "/login")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value(containsString("이메일은 50자를 초과할 수 없습니다.")));
      }
    }

    @Nested
    @DisplayName("만약 유효하지 않은 비밀번호로 로그인 요청을 하면")
    class Context_with_invalid_password {

      @Test
      @DisplayName("비밀번호가 누락되었다는 응답을 반환한다")
      void it_returns_missing_password_error() throws Exception {
        // given
        LoginRequest request = new LoginRequest(email, "");

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
      @DisplayName("비밀번호가 너무 짧다는 응답을 반환한다")
      void it_returns_password_too_short_error() throws Exception {
        // given
        String tooShortPassword = "1234567";
        LoginRequest request = new LoginRequest(email, tooShortPassword);

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
      @DisplayName("비밀번호가 너무 길다는 응답을 반환한다")
      void it_returns_password_too_long_error() throws Exception {
        // given
        String tooLongPassword = "verylongpasswordthatexceedstwentycharacters";
        LoginRequest request = new LoginRequest(email, tooLongPassword);

        // when & then
        mockMvc.perform(post(BASE_URL + "/login")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
      }

    }

    @Nested
    @DisplayName("만약 인증에 실패하면")
    class Context_with_authentication_failure {

      @Test
      @DisplayName("존재하지 않는 사용자라는 응답을 반환한다")
      void it_returns_user_not_found_error() throws Exception {
        // given
        LoginRequest request = new LoginRequest(email, password);

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
      @DisplayName("이메일 또는 비밀번호가 잘못되었다는 응답을 반환한다")
      void it_returns_invalid_email_or_password_error() throws Exception {
        // given
        String wrongPassword = "password456";

        // 사용자 미리 생성
        User user = createUser(email, nickname, passwordEncoder.encode(password));
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
    }

    @Nested
    @DisplayName("만약 요청 형식이 잘못되면")
    class Context_with_invalid_request_format {
      @Test
      @DisplayName("요청 형식이 올바르지 않다는 응답을 반환한다")
      void it_returns_missing_request_error() throws Exception {
        // when & then
        mockMvc.perform(post(BASE_URL + "/login")
                   .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
               .andExpect(jsonPath("$.message").value("요청 본문이 누락되었거나 형식이 올바르지 않습니다."));
      }
    }

  }

  @Nested
  @DisplayName("logout 메서드는")
  class Describe_logout {
    @Nested
    @DisplayName("만약 유효한 로그아웃 요청이면")
    class Context_with_valid_logout_request{
      @Test
      @DisplayName("로그아웃에 성공한다")
      void it_returns_logout_success_response() throws Exception {
        // when & then
        mockMvc.perform(post(BASE_URL + "/logout"))
               .andDo(print())
               .andExpect(status().isOk());
      }
    }
  }
}
