
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

  private final Long userId = 1L;
  private final String testEmail = "test@example.com";
  private final String secretKey = "testsecretkeyforjwttokengenerationandvalidation";
  private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

  private final long tokenValidity = 60000L; // 1분
  private final JwtUtil jwtUtil = new JwtUtil(secretKey, tokenValidity);

  @Test
  @DisplayName("JWT 토큰 생성 - 성공")
  void createToken_ValidUserId_ReturnsToken() {
    // when
    String token = jwtUtil.createToken(userId);

    // then
    assertThat(token).isNotBlank(); // null이 아니고, 빈 문자열이 아니고, 공백만 있지도 않은지 체크
    assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성됨 (header.payload.signature)
  }

  @Test
  @DisplayName("JWT 토큰 생성 - 다른 이메일로 다른 토큰 생성")
  void createToken_DifferentUserIds_ReturnsDifferentTokens() {
    // given
    Long anotherUserId = 2L;

    // when
    String token1 = jwtUtil.createToken(userId);
    String token2 = jwtUtil.createToken(anotherUserId);

    // then
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("JWT 토큰 검증 - 올바른 클레임 확인")
  void createToken_ValidUserId_ContainsCorrectClaims() {
    // when
    String token = jwtUtil.createToken(userId);

    // then - 토큰을 파싱하여 클레임 확인
    Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

    assertThat(claims.getSubject()).isEqualTo(userId.toString());
    assertThat(claims.getIssuedAt()).isNotNull();
    assertThat(claims.getExpiration()).isNotNull();

    // 만료 시간이 현재 시간 + tokenValidity 근처인지 확인 (오차 10초 허용)
    long expectedExpiry = System.currentTimeMillis() + tokenValidity;
    long actualExpiry = claims.getExpiration().getTime();
    assertThat(Math.abs(expectedExpiry - actualExpiry)).isLessThan(10000); // 10초 오차 허용
  }

  @Test
  @DisplayName("JWT 토큰 생성 - 반복 생성시 매번 다른 토큰")
  void createToken_SameUserIdMultipleTimes_ReturnsDifferentTokens() throws InterruptedException {
    // given

    // when
    String token1 = jwtUtil.createToken(userId);
    Thread.sleep(1_000); // 시간 차이를 두기 위해 1초 대기, JWT의 iat(issued at) 클레임은 초 단위의 Unix timestamp로 저장
    String token2 = jwtUtil.createToken(userId);

    // then
    assertThat(token1).isNotEqualTo(token2); // 발급 시간이 다르므로 토큰도 달라야 함
  }

  @Test
  @DisplayName("JWT 토큰 생성 - null userId 예외 발생")
  void createToken_NullUserId_ThrowsException() {
    // given

    // when & then
    assertThatThrownBy(() -> jwtUtil.createToken(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("아이디는 필수입니다.");
  }
}