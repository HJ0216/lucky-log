package com.fortunehub.luckylog.config;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    Member member = new Member(
        annotation.email(),
        "encodedPassword",
        annotation.nickname()
    );
    ReflectionTestUtils.setField(member, "id", annotation.id());

    CustomUserDetails userDetails = new CustomUserDetails(member);

    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    return context;
  }
}
