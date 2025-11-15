package com.fortunehub.luckylog.security;

import com.fortunehub.luckylog.domain.member.Member;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private static final String DEFAULT_DISPLAY_NAME = "내 정보 보기";

  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // 만료 기능 없어 true
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getDisplayName() {
    if (member.getNickname() != null && !member.getNickname().isBlank()) {
      return member.getNickname();
    }

    String email = member.getEmail();
    if (email == null) {
      return DEFAULT_DISPLAY_NAME;
    }

    // 이메일에서 @ 앞부분 추출
    int atIndex = email.indexOf('@');
    return atIndex > 0 ? email.substring(0, atIndex) : DEFAULT_DISPLAY_NAME;
  }
}
