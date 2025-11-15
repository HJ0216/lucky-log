package com.fortunehub.luckylog.controller.web.auth;

import com.fortunehub.luckylog.controller.web.auth.form.SignupForm;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
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
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

  public static final String SIGNUP_VIEW = "auth/signup";
  public static final String LOGIN_VIEW = "redirect:/login";

  private final AuthService authService;

  @GetMapping
  public String show(@ModelAttribute SignupForm form) {
    // @ModelAttribute는 넘어오는 데이터가 없어도 자동으로 빈 객체를 생성
    // 매개변수 이름과 무관하게 form 객체 이름 사용
    return SIGNUP_VIEW;
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute SignupForm form,
      BindingResult result
  ) {

    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.warn(
              "[회원가입 검증 실패] - [입력값 유효성 오류] | field={} | message={}",
              error.getField(), error.getDefaultMessage())
      );

      return SIGNUP_VIEW;
    }

    try {
      authService.signup(SignupRequest.from(form));

      return LOGIN_VIEW;

    } catch (CustomException e) {
      switch (e.getErrorCode()) {
        case DUPLICATE_EMAIL ->
            result.rejectValue("email", e.getErrorCode().name(), e.getMessage());
        case DUPLICATE_NICKNAME ->
            result.rejectValue("nickname", e.getErrorCode().name(), e.getMessage());
        default -> result.addError(
            new ObjectError(result.getObjectName(), ErrorCode.SIGNUP_SYSTEM_ERROR.getMessage()));
      }

      return SIGNUP_VIEW;
    } catch (Exception e) {
      log.error("[회원가입 실패] - [시스템 예외 발생]", e);

      result.addError(
          new ObjectError(result.getObjectName(), ErrorCode.SIGNUP_SYSTEM_ERROR.getMessage()));

      return SIGNUP_VIEW;
    }
  }
}
