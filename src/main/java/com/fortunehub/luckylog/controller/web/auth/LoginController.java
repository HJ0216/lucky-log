package com.fortunehub.luckylog.controller.web.auth;

import com.fortunehub.luckylog.controller.web.auth.form.LoginForm;
import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

  private static final String LOGIN_VIEW = "auth/login";
  private static final String REDIRECT_HOME = "redirect:/";

  private final AuthService authService;

  @GetMapping
  public String show(@ModelAttribute LoginForm form) {
    return LOGIN_VIEW;
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute LoginForm form,
      BindingResult result
  ) {
    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.warn(
              "[로그인 검증 실패] - [입력값 유효성 오류] | field={} | message={}",
              error.getField(), error.getDefaultMessage())
      );

      return LOGIN_VIEW;
    }

    try {
      authService.login(LoginRequest.from(form));

      return REDIRECT_HOME;
    } catch (CustomException e) {
      result.addError(
          new ObjectError(result.getObjectName(), e.getMessage()));

      return LOGIN_VIEW;
    } catch (Exception e) {
      log.error("[로그인 실패] - [시스템 예외 발생]", e);

      result.addError(
          new ObjectError(result.getObjectName(), ErrorCode.LOGIN_SYSTEM_ERROR.getMessage()));

      return LOGIN_VIEW;
    }
  }
}
