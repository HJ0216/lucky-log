
package com.fortunehub.luckylog.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

  private final String testEmail = "test@example.com";
  private final String secretKey = "testsecretkeyforjwttokengenerationandvalidation";
  private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

  private final long tokenValidity = 60000L; // 1분
  private final JwtUtil jwtUtil = new JwtUtil(secretKey, tokenValidity);

  @Test
  @DisplayName("JWT 토큰 생성 - 성공")
  void createToken_ValidEmail_ReturnsToken() {
    // when
    String token = jwtUtil.createToken(testEmail);

    // then
    assertThat(token).isNotBlank(); // null이 아니고, 빈 문자열이 아니고, 공백만 있지도 않은지 체크
    assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성됨 (header.payload.signature)
  }

  @Test
  @DisplayName("JWT 토큰 생성 - 다른 이메일로 다른 토큰 생성")
  void createToken_DifferentEmails_ReturnsDifferentTokens() {
    // given
    String email1 = "user1@example.com";
    String email2 = "user2@example.com";

    // when
    String token1 = jwtUtil.createToken(email1);
    String token2 = jwtUtil.createToken(email2);

    // then
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("JWT 토큰 검증 - 올바른 클레임 확인")
  void createToken_ValidEmail_ContainsCorrectClaims() {
    // when
    String token = jwtUtil.createToken(testEmail);

    // then - 토큰을 파싱하여 클레임 확인
    Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

    assertThat(claims.getSubject()).isEqualTo(testEmail);
    assertThat(claims.getIssuedAt()).isNotNull();
    assertThat(claims.getExpiration()).isNotNull();

    // 만료 시간이 현재 시간 + tokenValidity 근처인지 확인 (오차 10초 허용)
    long expectedExpiry = System.currentTimeMillis() + tokenValidity;
    long actualExpiry = claims.getExpiration().getTime();
    assertThat(Math.abs(expectedExpiry - actualExpiry)).isLessThan(10000); // 10초 오차 허용
  }

  @Test
  @DisplayName("JWT 토큰 생성 - 반복 생성시 매번 다른 토큰")
  void createToken_SameEmailMultipleTimes_ReturnsDifferentTokens() throws InterruptedException {
    // given
    String email = "test@example.com";

    // when
    String token1 = jwtUtil.createToken(email);
    Thread.sleep(1_000); // 시간 차이를 두기 위해 1초 대기, JWT의 iat(issued at) 클레임은 초 단위의 Unix timestamp로 저장
    String token2 = jwtUtil.createToken(email);

    // then
    assertThat(token1).isNotEqualTo(token2); // 발급 시간이 다르므로 토큰도 달라야 함
  }

  @Test
  @DisplayName("JWT 토큰 생성 - 빈 이메일 예외 발생")
  void createToken_EmptyEmail_ThrowsException() {
    // given
    String emptyEmail = "";

    // when & then
    assertThatThrownBy(() -> jwtUtil.createToken(emptyEmail))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이메일은 필수입니다.");
  }

  @Test
  @DisplayName("JWT 토큰 생성 - null 이메일 예외 발생")
  void createToken_NullEmail_ThrowsException() {
    // when & then
    assertThatThrownBy(() -> jwtUtil.createToken(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이메일은 필수입니다.");
  }
}