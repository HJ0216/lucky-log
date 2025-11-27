package com.fortunehub.luckylog.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.fixture.MemberFixture;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class) // Mockito 관련 애노테이션(@Mock, @InjectMocks 등)을 자동으로 초기화
class CustomUserDetailsServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks // Mockito가 AuthService 객체 생성 + mock 의존성 주입
  private CustomUserDetailsService userDetailsService;

  @Test
  @DisplayName("이메일로 사용자를 찾으면 UserDetails를 반환한다")
  void loadUserByUsername_WhenUserExists_ThenReturnsUserDetails() {
    // given
    Member member = MemberFixture.createMember();
    given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

    // when
    UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

    // then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
    assertThat(userDetails.getUsername()).isEqualTo(member.getEmail());

    verify(memberRepository).findByEmail(member.getEmail());
  }

  @Test
  @DisplayName("사용자를 찾지 못하면 UsernameNotFoundException을 던진다")
  void loadUserByUsername_WhenUserNotFound_ThenThrowsException() {
    // given
    String notFoundEmail = "notfound@example.com";
    given(memberRepository.findByEmail(notFoundEmail)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(notFoundEmail))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("사용자를 찾을 수 없습니다");

    verify(memberRepository).findByEmail(notFoundEmail);
  }

  @Test
  @DisplayName("비활성 회원도 이메일로 조회할 수 있다")
  void loadUserByUsername_WhenMemberInactive_ThenReturnsUserDetails() {
    // given
    Member member = MemberFixture.createInactiveMember();

    given(memberRepository.findByEmail(member.getEmail()))
        .willReturn(Optional.of(member));

    // when
    UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

    // then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.isEnabled()).isFalse();
  }
}