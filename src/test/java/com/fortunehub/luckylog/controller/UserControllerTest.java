package com.fortunehub.luckylog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.fortunehub.luckylog.service.UserService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
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

  @Test
  @DisplayName("유효한 사용자 생성 요청 - 성공")
  void createUser_ValidRequest_Success() throws Exception {
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
           .andExpect(status().isOk()); // HTTP 상태 코드가 200 OK인지 확인
  }

  @Test
  @DisplayName("이메일 필드가 비어있는 경우 - 실패")
  void createUser_EmptyEmail_Fail() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "",
        "test",
        "password123"
    );

    mockMvc.perform(post("/api/v1/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("이메일 형식이 잘못된 경우 - 실패")
  void createUser_InvalidEmailFormat_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("이메일이 50자를 초과하는 경우 - 실패")
  void createUser_EmailTooLong_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("닉네임이 비어있는 경우 - 실패")
  void createUser_EmptyNickname_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("닉네임이 2자 미만인 경우 - 실패")
  void createUser_NicknameTooShort_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("닉네임이 8자를 초과하는 경우 - 실패")
  void createUser_NicknameTooLong_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("비밀번호가 비어있는 경우 - 실패")
  void createUser_EmptyPassword_Fail() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "testuser",
        "" // 빈 비밀번호
    );

    // when & then
    mockMvc.perform(post("/api/v1/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("비밀번호가 8자 미만인 경우 - 실패")
  void createUser_PasswordTooShort_Fail() throws Exception {
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
           .andExpect(status().isBadRequest());
  }
  
  @Test
  @DisplayName("유저 조회 - 성공")
  void getUser_ValidRequest_Success() throws Exception{
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
  @DisplayName("유저 조회 - 존재하지 않는 유저")
  void getUser_UserNotFound() throws Exception {
    // given
    Long userId = 100L;
    when(userService.getUser(userId))
        .thenThrow(new IllegalArgumentException());

    // when & then
    Exception exception = assertThrows(ServletException.class, () -> {
      mockMvc.perform(get("/api/v1/user/{id}", userId))
             .andDo(print());
    });

    assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
  }


}