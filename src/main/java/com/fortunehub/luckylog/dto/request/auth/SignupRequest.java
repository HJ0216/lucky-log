package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.controller.web.auth.form.SignupForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class SignupRequest {

  private final String email;
  private final String password;
  private final String nickname;

  public static SignupRequest from(SignupForm form) {
    String cleanedNickname =
        StringUtils.hasText(form.getNickname()) ? form.getNickname().trim() : null;
    return new SignupRequest(form.getEmail(), form.getPassword(), cleanedNickname);
  }
}
