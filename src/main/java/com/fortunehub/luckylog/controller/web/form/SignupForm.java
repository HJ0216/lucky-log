package com.fortunehub.luckylog.controller.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SignupForm {

  @NotNull(message = "📧 이메일을 입력해주세요!")
  private String email;

  @NotNull(message = "🔒 비밀번호를 입력해주세요!")
  private String password;

  private String nickname;

}
