package com.fortunehub.luckylog.security;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.domain.member.Role;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, Serializable {

  private static final String DEFAULT_DISPLAY_NAME = "내 정보 보기";

  private Long memberId;
  private String email;
  private String password;
  private String nickname;
  private Role role;
  private boolean isActive;

  public CustomUserDetails(Member member) {
    memberId = member.getId();
    email = member.getEmail();
    password = member.getPassword();
    nickname = member.getNickname();
    role = member.getRole();
    isActive = member.isActive();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
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
    return isActive;
  }

  public String getDisplayName() {
    if (nickname != null && !nickname.isBlank()) {
      return nickname;
    }

    if (email == null) {
      return DEFAULT_DISPLAY_NAME;
    }

    // 이메일에서 @ 앞부분 추출
    int atIndex = email.indexOf('@');
    return atIndex > 0 ? email.substring(0, atIndex) : DEFAULT_DISPLAY_NAME;
  }
}
