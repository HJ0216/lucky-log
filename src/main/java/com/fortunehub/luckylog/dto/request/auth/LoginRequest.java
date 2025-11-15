package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.controller.web.auth.form.LoginForm;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

  private String email;
  private String password;

  public static LoginRequest from(LoginForm form) {
    return new LoginRequest(form.getEmail(), form.getPassword());
  }
}
