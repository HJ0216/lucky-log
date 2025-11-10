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
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
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
@WebMvcTest(SignupController.class)
// 웹 계층만 로드해서 테스트
@DisplayName("회원가입 Controller")
class SignupControllerTest {

  @Autowired
  private MockMvc mockMvc;
  // 실제 서버를 띄우지 않고도 Controller를 테스트할 수 있게 해주는 Spring의 테스트 도구
  // HTTP 요청을 가짜로 만듦 → HTTP 요청을 가짜로 만듦 → Controller의 응답을 받아서 → 결과를 검증

  @MockitoBean
  private AuthService authService;

  @Test
  @DisplayName("회원가입 페이지를 요청하면 정상적으로 보여준다")
  void showSignupPage() throws Exception {
    // when & then
    mockMvc.perform(get("/signup"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeExists("signupForm"));
  }

  @Test
  @DisplayName("올바른 회원 정보로 회원가입하면 메인 페이지로 리다이렉트된다")
  void signup_Success() throws Exception {
    // given
    doNothing().when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/"));
  }

  @Test
  @DisplayName("필수 필드가 비어있으면 검증 오류를 표시한다")
  void signup_EmptyRequiredFields() throws Exception {
    // when & then
    mockMvc.perform(post("/signup"))
           // 아무 파라미터도 보내지 않음
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().hasErrors())
           .andExpect(model().attributeHasFieldErrors("signupForm", "email"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "password"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "confirmPassword"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "passwordMatched"));

    verify(authService, never()).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("이메일 형식이 잘못되면 회원가입 페이지를 다시 보여준다")
  void signup_InvalidEmail() throws Exception {
    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "invalid-email")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().hasErrors())
           .andExpect(model().attributeHasFieldErrors("signupForm", "email"));

    verify(authService, never()).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 가입하면 검증 오류를 표시한다")
  void signup_DuplicateEmail() throws Exception {
    // given
    doThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL))
        .when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "duplicate@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().hasErrors())
           .andExpect(model().attributeHasFieldErrors("signupForm", "email"));
  }

  @Test
  @DisplayName("조건에 맞지 않는 비밀번호로 가입하면 검증 오류를 표시한다")
  void signup_WeakPassword() throws Exception {
    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "12345678")
               .param("confirmPassword", "12345678")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "password"));
  }

  @Test
  @DisplayName("비밀번호와 비밀번호 확인이 불일치하면 검증 오류를 표시한다")
  void submitSignup_PasswordMismatch() throws Exception {
    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@example.com")
               .param("password", "Password123!")
               .param("confirmPassword", "DifferentPass123!")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().hasErrors())
           .andExpect(model().attributeHasFieldErrors("signupForm", "passwordMatched"));

    verify(authService, never()).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("이미 존재하는 닉네임으로 가입하면 검증 오류를 표시한다")
  void signup_DuplicateNickname() throws Exception {
    // given
    doThrow(new CustomException(ErrorCode.DUPLICATE_NICKNAME))
        .when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "duplicate@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().hasErrors())
           .andExpect(model().attributeHasFieldErrors("signupForm", "nickname"));
  }

  @Test
  @DisplayName("조건에 맞지 않는 닉네임로 가입하면 검증 오류를 표시한다")
  void signup_NicknameLengthValidation() throws Exception {
    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "a"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "nickname"));
  }

  @Test
  @DisplayName("예상치 못한 오류 발생 시, 에러 페이지로 이동한다")
  void signup_UnexpectedError() throws Exception {
    // given
    doThrow(new RuntimeException("예상치 못한 오류 발생"))
        .when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/error/5xx"));
  }
}