package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequest {

  private String email;
  private String password;
  private String nickname;

  public Member toEntity(String encodedPassword){
    return new Member(email, encodedPassword, nickname);
  }
}
