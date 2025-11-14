package com.fortunehub.luckylog.controller.web.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fortunehub.luckylog.config.TestSecurityConfig;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.dto.session.SessionMember;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
  private static final String TEST_NOT_FOUND_EMAIL = "unlucky@email.com";
  private static final String TEST_RAW_PASSWORD = "password147@";
  private static final String TEST_ENCODED_PASSWORD = "encodedPassword123";
  private static final String TEST_NICKNAME = "솜사탕";

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
    Member member = new Member(TEST_EMAIL, TEST_ENCODED_PASSWORD, TEST_NICKNAME);
    given(authService.login(any(LoginRequest.class))).willReturn(member);

    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", TEST_RAW_PASSWORD))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/"))
           .andExpect(request().sessionAttribute("loginMember", notNullValue()));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("로그인 성공 시 세션에 회원 정보가 저장된다")
  void submit_WhenLoginSuccess_ThenSavesSessionMember() throws Exception {
    // given
    Member member = new Member(TEST_EMAIL, TEST_ENCODED_PASSWORD, TEST_NICKNAME);
    given(authService.login(any(LoginRequest.class))).willReturn(member);

    // when & then
    MvcResult result = mockMvc.perform(post("/login")
                                  .param("email", TEST_EMAIL)
                                  .param("password", TEST_RAW_PASSWORD))
                              .andExpect(status().is3xxRedirection())
                              .andReturn();

    MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
    SessionMember sessionMember = (SessionMember) session.getAttribute("loginMember");

    assertThat(sessionMember).isNotNull();
    assertThat(sessionMember.getEmail()).isEqualTo(TEST_EMAIL);
    assertThat(sessionMember.getNickname()).isEqualTo(TEST_NICKNAME);
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
  @DisplayName("존재하지 않는 계정으로 로그인 시 오류 메시지가 표시된다")
  void submit_WhenAccountNotExists_ThenReturnsErrorMessage() throws Exception {
    // given
    given(authService.login(any(LoginRequest.class)))
        .willThrow(new CustomException(ErrorCode.LOGIN_FAILED));

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
  @DisplayName("잘못된 비밀번호로 로그인 시 오류 메시지가 표시된다")
  void submit_WhenPasswordIsWrong_ThenReturnsErrorMessage() throws Exception {
    // given
    given(authService.login(any(LoginRequest.class)))
        .willThrow(new CustomException(ErrorCode.LOGIN_FAILED));

    // when & then
    mockMvc.perform(post("/login")
               .param("email", TEST_EMAIL)
               .param("password", "wrongPassword"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/login"))
           .andExpect(model().attributeHasErrors("loginForm"));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  @DisplayName("시스템 예외 발생 시 에러 메시지가 표시된다")
  void submit_WhenSystemExceptionOccurs_ThenReturnsErrorMessage() throws Exception {
    // given
    given(authService.login(any(LoginRequest.class)))
        .willThrow(new RuntimeException("Runtime Exception"));

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