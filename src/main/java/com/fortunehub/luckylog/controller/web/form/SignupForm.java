package com.fortunehub.luckylog.controller.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SignupForm {

  @NotBlank(message = "📧 이메일을 입력해주세요!")
  @Email(message = "📧 올바른 이메일 형식이 아닙니다")
  private String email;

  @NotBlank(message = "🔒 비밀번호를 입력해주세요!")
  @Size(min = 8, max = 20, message = "🔒 비밀번호는 8-20자 사이여야 합니다!")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
      message = "🔒 영문 + 숫자 + 특수문자 조합이어야 합니다"
  )
  private String password;

  @NotBlank(message = "🔐 비밀번호 확인을 입력해주세요!")
  private String confirmPassword;

  @Size(min = 2, max = 20, message = "✨ 닉네임은 2-20자 사이여야 합니다!")
  private String nickname;
}
