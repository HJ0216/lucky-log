package com.fortunehub.luckylog.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.fixture.MemberFixture;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class CustomUserDetailsTest {

  private Member member;

  @BeforeEach
  void setUp() {
    member = MemberFixture.createMember();
  }

  @Test
  @DisplayName("닉네임이 있으면 닉네임을 반환한다")
  void getDisplayName_WhenNicknameExists_ThenReturnsNickname() {
    // given
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    String displayName = userDetails.getDisplayName();

    // then
    assertThat(displayName).isEqualTo(member.getNickname());
  }

  @Test
  @DisplayName("닉네임이 없으면 이메일 앞부분을 반환한다")
  void getDisplayName_WhenNicknameNull_ThenReturnsEmailId() {
    // given
    Member member = MemberFixture.createMember("lucky@email.com", null);
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    String displayName = userDetails.getDisplayName();

    // then
    assertThat(displayName).isEqualTo("lucky");
  }

  @Test
  @DisplayName("이메일에 @가 없으면 기본값을 반환한다")
  void getDisplayName_WhenEmailInvalid_ThenReturnsDefaultValue() {
    // given
    Member member = MemberFixture.createMember("invalid-email", null);
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    String displayName = userDetails.getDisplayName();

    // then
    assertThat(displayName).isEqualTo("내 정보 보기");
  }

  @Test
  @DisplayName("getUsername은 이메일을 반환한다")
  void getUsername_ReturnsEmail() {
    // given
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    String username = userDetails.getUsername();

    // then
    assertThat(username).isEqualTo(member.getEmail());
  }

  @Test
  @DisplayName("isEnabled는 회원의 활성 상태를 반환한다")
  void isEnabled_ReturnsIsActive() {
    // given
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    boolean isEnabled = userDetails.isEnabled();

    // then
    assertThat(isEnabled).isTrue();
  }

  @Test
  @DisplayName("권한은 ROLE_USER를 반환한다")
  void getAuthorities_ReturnsRole() {
    // given
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    // then
    assertThat(authorities).hasSize(1);
    assertThat(authorities)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_USER");
  }

  @Test
  @DisplayName("getPassword는 암호화된 비밀번호를 반환한다")
  void getPassword_ReturnsPassword() {
    // given
    CustomUserDetails userDetails = new CustomUserDetails(member);

    // when
    String password = userDetails.getPassword();

    // then
    assertThat(password).isEqualTo(member.getPassword());
  }
}