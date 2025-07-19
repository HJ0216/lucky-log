package com.fortunehub.luckylog.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.config.SecurityConfig;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.exception.GlobalExceptionHandler;
import com.fortunehub.luckylog.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  // perform() 메서드를 통해 가상의 HTTP 요청을 보냄

  @MockitoBean // 가짜(mock) 객체
  private UserService userService;
  // 컨트롤러가 이 서비스를 호출하더라도 실제 로직이 실행되지 않고, 모킹된 결과만 제공

  @Autowired
  private ObjectMapper objectMapper;
  // 자바 객체를 JSON 문자열로 변환 또는 JSON 문자열을 자바 객체로 변환

  // ========== 이메일 중복 검사 API 테스트 ==========
  @Test
  @DisplayName("이메일 중복 검사 - 사용 가능")
  void checkEmailDuplicate_AvailableEmail_ReturnsOk() throws Exception {    // given
    String email = "new@email.com";

    // when
    // Mock 설정 없음 → userService.isEmailAvailable(email) 기본값 false 리턴
    when(userService.isEmailAvailable(email)).thenReturn(true);

    // then
    mockMvc.perform((get("/api/v1/user/check-email")
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

    // when
    when(userService.isEmailAvailable(email)).thenReturn(false);

    // then
    mockMvc.perform((get("/api/v1/user/check-email")
               .param("email", email)))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.available").value(false))
           .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 파라미터 누락")
  void checkEmailDuplicate_MissingEmail_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/user/check-email"))
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
    mockMvc.perform(post("/api/v1/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request))) // request 객체를 JSON 문자열로 바꿔서 전송
           .andDo(print()) // 요청/응답 결과를 콘솔에 출력
           // HTTP 상태 코드가 200 OK인지 확인
           .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("회원 가입 - 이메일 필드 누락")
  void createUser_EmptyEmail_ReturnsBadRequest() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "",
        "test",
        "password123"
    );

    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
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
    mockMvc.perform(post("/api/v1/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value("password: 비밀번호는 8자 이상 20자 이하여야 합니다."));
  }

  
  // ========== 회원 조회 API 테스트 ==========
  @Test
  @DisplayName("회원 조회 - 성공")
  void getUser_ValidId_ReturnsOk() throws Exception{
    // given
    long userId = 1L;
    String email = "test@email.com";
    String nickname = "text";

    UserResponse response = new UserResponse(email, nickname);

    when(userService.getUser(userId)).thenReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/user/{id}", userId))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.email").value(email))
           .andExpect(jsonPath("$.nickname").value(nickname));
  }

  @Test
  @DisplayName("회원 조회 - 존재하지 않는 회원")
  void getUser_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    Long userId = 100L;
    when(userService.getUser(userId))
        .thenThrow(new IllegalArgumentException("존재하지 않는 사용자입니다."));

    // when & then
    mockMvc.perform(get("/api/v1/user/{id}", userId))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
  }
}