package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.controller.web.auth.form.SignupForm;
import com.fortunehub.luckylog.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequest {

  private String email;
  private String password;
  private String nickname;

  public static SignupRequest from(SignupForm form){
    String cleanedNickname = (form.getNickname() == null || form.getNickname().isBlank()) ? null : form.getNickname().trim();
    return new SignupRequest(form.getEmail(), form.getPassword(), cleanedNickname);
  }
}
