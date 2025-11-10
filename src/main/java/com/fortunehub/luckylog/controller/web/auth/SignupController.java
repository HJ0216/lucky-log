package com.fortunehub.luckylog.controller.web.auth;

import com.fortunehub.luckylog.controller.web.auth.form.SignupForm;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

  private final AuthService authService;

  @GetMapping
  public String show(@ModelAttribute SignupForm form) {
    // @ModelAttribute는 넘어오는 데이터가 없어도 자동으로 빈 객체를 생성
    // 매개변수 이름과 무관하게 form 객체 이름 사용
    return "auth/signup";
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute SignupForm form,
      BindingResult result
  ) {

    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.warn("회원가입 검증 실패 - 필드: {}, 입력값: {}, 메시지: {}",
              error.getField(),
              error.getRejectedValue(),
              error.getDefaultMessage())
      );

      return "auth/signup";
    }

    try {
      authService.signup(form.toRequest());

      log.info("회원가입 성공 - 이메일: {}", form.getEmail());

//      return "redirect:/login";
      return  "redirect:/";

    } catch (CustomException e) {
      switch (e.getErrorCode()){
        case DUPLICATE_EMAIL:
          result.rejectValue("email", e.getErrorCode().name(), e.getMessage());
          log.warn("중복 이메일로 가입 시도: {}", form.getEmail());
          break;
        case DUPLICATE_NICKNAME:
          result.rejectValue("nickname", e.getErrorCode().name(), e.getMessage());
          log.warn("중복 닉네임으로 가입 시도: {}", form.getNickname());
          break;
      }

      return "auth/signup";
    }
  }
}
