package com.fortunehub.luckylog.controller.web.auth.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginForm {

  @NotBlank(message = "📧 이메일을 입력해주세요!")
  private String email;

  @NotBlank(message = "🔒 비밀번호를 입력해주세요!")
  private String password;
}
