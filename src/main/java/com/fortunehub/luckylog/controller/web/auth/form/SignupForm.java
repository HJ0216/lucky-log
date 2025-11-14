package com.fortunehub.luckylog.controller.web.auth.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SignupForm {

  @NotBlank(message = "📧 이메일을 입력해주세요!")
  @Pattern(
      regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
      message = "📧 올바른 이메일 형식이 아닙니다"
  )
  private String email;

  @NotBlank(message = "🔒 비밀번호를 입력해주세요!")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
      message = "🔒 8-20자의 영문, 숫자, 특수문자 조합이어야 합니다!"
  )
  private String password;

  @NotBlank(message = "🔐 비밀번호 확인을 입력해주세요!")
  private String confirmPassword;

  // optional, 입력 시 2-20자 사이
  @Pattern(
      regexp = "^$|^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9\\s]{2,20}$",
      message = "✨ 닉네임은 2-20자의 한글, 영문, 숫자, 띄어쓰기만 가능합니다!"
  )
  private String nickname;

  public void setNickname(String nickname) {
    // 1. setter 호출 후 trim
    // 2. @Pattern 검증
    this.nickname = (nickname == null) ? null : nickname.trim();
  }

  @AssertTrue(message = "😮 비밀번호가 일치하지 않습니다!")
  public boolean isPasswordMatched() {
    return password != null && password.equals(confirmPassword);
  }
}
