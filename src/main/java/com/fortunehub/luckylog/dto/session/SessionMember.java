package com.fortunehub.luckylog.dto.session;

import com.fortunehub.luckylog.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionMember {

  public static final String DEFAULT_NICKNAME = "솜사탕 구름";

  private Long id;
  private String email;
  private String nickname;

  public static SessionMember from(Member member) {
    return new SessionMember(member.getId(), member.getEmail(), member.getNickname());
  }

  public String getDisplayName(){
    if (nickname != null && !nickname.isBlank()) {
      return nickname;
    }

    if (email != null) {
      int atIndex = email.indexOf('@');
      return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    return DEFAULT_NICKNAME;
  }
}
