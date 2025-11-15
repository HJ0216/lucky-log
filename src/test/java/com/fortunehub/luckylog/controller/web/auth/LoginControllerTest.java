package com.fortunehub.luckylog.controller.web.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fortunehub.luckylog.config.TestSecurityConfig;
import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(TestSecurityConfig.class)
@WebMvcTest(LoginController.class)
// 웹 계층만 로드해서 테스트
@DisplayName("로그인 Controller")
class LoginControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;

  private static final String TEST_EMAIL = "lucky@email.com";
  private static final String TEST_RAW_PASSWORD = "password147@";

  @Test
  @DisplayName("로그인 페이지 요청 시 정상적으로 렌더링된다")
  void show_WhenRequested_ThenReturnsLoginView() throws Exception {
    // when & then
    mockMvc.perform(get("/login"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeExists("loginForm"));
  }

  @Test
  @DisplayName("유효한 회원정보로 로그인 시 메인 페이지로 리다이렉트된다")
  void submit_WhenValidData_ThenRedirectsToIndex() throws Exception {
    // given
    doNothing().when(authService).login(any(LoginRequest.class));

    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", TEST_RAW_PASSWORD))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/"));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("빈 이메일로 로그인 시 검증 오류가 발생한다")
  void submit_WhenEmailIsEmpty_ThenReturnsValidationError() throws Exception {
    // when & then
    mockMvc.perform(post("/login")
               .param("email", "")
               .param("password", TEST_RAW_PASSWORD))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeHasFieldErrors("loginForm", "email"));

    verify(authService, never()).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("인증 실패 시 오류 메시지가 표시된다")
  void submit_WhenAuthenticationFails_ThenReturnsErrorMessage() throws Exception {
    // given
    doThrow(new CustomException(ErrorCode.LOGIN_FAILED))
        .when(authService).login(any(LoginRequest.class));

    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", TEST_RAW_PASSWORD))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeHasErrors("loginForm"))
           .andExpect(model().attributeErrorCount("loginForm", 1));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("빈 비밀번호로 로그인 시 검증 오류가 발생한다")
  void submit_WhenPasswordIsEmpty_ThenReturnsValidationError() throws Exception {
    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", ""))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeHasFieldErrors("loginForm", "password"));

    verify(authService, never()).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("시스템 예외 발생 시 에러 메시지가 표시된다")
  void submit_WhenSystemExceptionOccurs_ThenReturnsErrorMessage() throws Exception {
    // given
    doThrow(new RuntimeException("Runtime Exception"))
        .when(authService).login(any(LoginRequest.class));

    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", TEST_RAW_PASSWORD))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeHasErrors("loginForm"))
           .andExpect(model().attributeErrorCount("loginForm", 1));

    verify(authService).login(any(LoginRequest.class));
  }
}