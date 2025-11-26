package com.fortunehub.luckylog.security;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
                                    .orElseThrow(() -> {
                                      log.warn("사용자 조회 실패 | email={}", email);
                                      return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                                    });

    return new CustomUserDetails(member);
  }
}
