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
  // HTTP 요청을 가짜로 만듦 → Controller의 응답을 받아서 → 결과를 검증

  @MockitoBean
  private AuthService authService;

  @Test
  @DisplayName("회원가입 페이지 요청 시 정상적으로 렌더링된다")
  void show_WhenRequested_ThenReturnsSignupView() throws Exception {
    // when & then
    mockMvc.perform(get("/signup"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeExists("signupForm"));
  }

  @Test
  @DisplayName("유효한 회원정보로 가입 시 로그인 페이지로 리다이렉트된다")
  void submit_WhenValidData_ThenRedirectsToLogin() throws Exception {
    // given
    doNothing().when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login"));

    verify(authService).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("필수 필드가 비어있으면 검증 오류를 표시한다")
  void submit_WhenRequiredFieldsMissing_ThenReturnsValidationErrors() throws Exception {
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
  @DisplayName("잘못된 이메일 형식으로 가입 시 검증 오류가 발생한다")
  void submit_WhenEmailInvalid_ThenReturnsValidationError() throws Exception {
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
  @DisplayName("중복된 이메일로 가입 시 검증 오류가 발생한다")
  void submit_WhenEmailDuplicated_ThenReturnsValidationError() throws Exception {
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
  @DisplayName("약한 비밀번호로 가입 시 검증 오류가 발생한다")
  void submit_WhenPasswordWeak_ThenReturnsValidationError() throws Exception {
    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "12345678")
               .param("confirmPassword", "12345678")
               .param("nickname", "테스터"))
           .andExpect(status().isOk())
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeHasFieldErrors("signupForm", "password"));

    verify(authService, never()).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("비밀번호 불일치 시 검증 오류가 발생한다")
  void submit_WhenPasswordMismatch_ThenReturnsValidationError() throws Exception {
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
  @DisplayName("닉네임에 띄어 쓰기가 있더라도 가입 후 로그인 페이지로 리다이렉트된다")
  void submit_WhenNicknameHasSpace_ThenRedirectsToLogin() throws Exception {
    // given
    doNothing().when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", " 닉 네 임 "))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login"));

    verify(authService).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("닉네임에 자음만 있어도 가입 후 로그인 페이지로 리다이렉트된다")
  void submit_WhenNicknameHasOnlyConsonants_ThenRedirectsToLogin() throws Exception {
    // given
    doNothing().when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "ㄱㄴㄷ"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login"));

    verify(authService).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("닉네임 없이 가입 시 로그인 페이지로 리다이렉트된다")
  void submit_WhenNicknameEmpty_ThenRedirectsToLogin() throws Exception {
    // given
    doNothing().when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", ""))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login"));

    verify(authService).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("중복된 닉네임으로 가입 시 검증 오류가 발생한다")
  void submit_WhenNicknameDuplicated_ThenReturnsValidationError() throws Exception {
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

    verify(authService).signup(any(SignupRequest.class));
  }

  @Test
  @DisplayName("잘못된 닉네임 길이로 가입 시 검증 오류가 발생한다")
  void submit_WhenNicknameInvalidLength_ThenReturnsValidationError() throws Exception {
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
  @DisplayName("예상치 못한 오류 발생 시 검증 오류가 발생한다")
  void submit_WhenUnexpectedError_ThenRedirectsToErrorPage() throws Exception {
    // given
    doThrow(new RuntimeException("예상치 못한 오류 발생"))
        .when(authService).signup(any(SignupRequest.class));

    // when & then
    mockMvc.perform(post("/signup")
               .param("email", "test@email.com")
               .param("password", "Password123!")
               .param("confirmPassword", "Password123!")
               .param("nickname", "테스터"))
           .andExpect(view().name("auth/signup"))
           .andExpect(model().attributeHasErrors("signupForm"));
  }
}