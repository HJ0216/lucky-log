package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.controller.web.auth.form.LoginForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

  private final String email;
  private final String password;

  public static LoginRequest from(LoginForm form) {
    return new LoginRequest(form.getEmail(), form.getPassword());
  }
}
