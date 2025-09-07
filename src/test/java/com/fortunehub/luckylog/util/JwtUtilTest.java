package com.fortunehub.luckylog.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

  private static final String SECRET_KEY = "testsecretkeyforjwttokengenerationandvalidation";
  private static final long TOKEN_VALIDITY = 60_000L;
  private final JwtUtil jwtUtil = new JwtUtil(SECRET_KEY, TOKEN_VALIDITY);

  @Nested
  @DisplayName("createToken()는")
  class Describe_createToken {

    @Nested
    @DisplayName("유효한 요청이면")
    class Context_with_valid_request {

      @Test
      @DisplayName("JWT를 반환한다")
      void it_returns_jwt_response() {
        // given

        // when
        String token = jwtUtil.createToken(1L);

        // then
        assertThat(token).isNotBlank(); // null이 아니고, 빈 문자열이 아니고, 공백만 있지도 않은지 체크
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성됨 (header.payload.signature)
      }

      @Test
      @DisplayName("서로 다른 userId를 가진 요청이면 다른 JWT를 반환한다")
      void it_returns_different_tokens_when_different_userIds_request() {
        // given

        // when
        String token1 = jwtUtil.createToken(1L);
        String token2 = jwtUtil.createToken(2L);

        // then
        assertThat(token1).isNotEqualTo(token2);
      }

      @Test
      @DisplayName("초까지 동일한 시간에 동일한 userId면 동일한 JWT를 반환한다")
      void it_returns_same_token_when_created_in_same_second() {
        // given

        // when
        String token1 = jwtUtil.createToken(1L);
        String token2 = jwtUtil.createToken(1L);

        // then
        assertThat(token1).isEqualTo(token2); // 발급 시간이 초단위까지 같으면 같음
      }

      @Test
      @DisplayName("올바른 클레임을 가진 JWT를 반환한다")
      void it_returns_jwt_containing_correct_claims() {
        // given
        Date testStartTime = new Date();

        // when
        String token = jwtUtil.createToken(1L);

        // then - 토큰을 파싱하여 클레임 확인
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey testKey = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(testKey)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();

        // 만료 시간이 현재 시간 + tokenValidity 근처인지 확인 (오차 10초 허용)
        long timeDiff = claims.getExpiration().getTime() - testStartTime.getTime();
        assertThat(timeDiff).isBetween(TOKEN_VALIDITY - 1000L, TOKEN_VALIDITY + 1000L);
      }
    }

    @Nested
    @DisplayName("userId가 null이면")
    class Context_with_null_userId {

      @Test
      @DisplayName("예외를 발생시킨다")
      void it_throws_exception() {
        // given

        // when, then
        assertThatThrownBy(() -> jwtUtil.createToken(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아이디는 필수입니다.");
      }
    }
  }

  @Nested
  @DisplayName("validateToken()는")
  class Describe_validateToken {

    @Nested
    @DisplayName("유효한 토큰이면")
    class Context_with_valid_token {

      @Test
      @DisplayName("true를 반환한다")
      void it_returns_true() {
        // given
        String validToken = jwtUtil.createToken(1L);

        // when
        boolean result = jwtUtil.validateToken(validToken);

        // then
        assertThat(result).isTrue();
      }
    }

    @Nested
    @DisplayName("유효하지 않은 토큰이면")
    class Context_with_invalid_token {

      @Test
      @DisplayName("잘못된 형식의 토큰에 대해 false를 반환한다")
      void it_returns_false_when_malformed_token() {
        // given
        String malformedToken = "invalid.token.format";

        // when
        boolean result = jwtUtil.validateToken(malformedToken);

        // then
        assertThat(result).isFalse();
      }

      @Test
      @DisplayName("빈 토큰값에 대해 false를 반환한다")
      void it_returns_false_when_empty_token() {
        // given
        String emptyToken = "";

        // when
        boolean result = jwtUtil.validateToken(emptyToken);

        // then
        assertThat(result).isFalse();
      }

      @Test
      @DisplayName("null 토큰값에 대해 false를 반환한다")
      void it_returns_false_when_null_token() {
        // given
        String nullToken = null;

        // when
        boolean result = jwtUtil.validateToken(nullToken);

        // then
        assertThat(result).isFalse();
      }

      @Test
      @DisplayName("잘못된 서명의 토큰에 대해 false를 반환한다")
      void it_returns_false_when_wrong_signature() {
        String differentSecretKey = "differentSecretKeyForTestingValidateTokenMethod";
        JwtUtil differentKeyJwtUtil = new JwtUtil(differentSecretKey, TOKEN_VALIDITY);
        String tokenWithDifferentKey = differentKeyJwtUtil.createToken(1L);

        // when
        boolean result = jwtUtil.validateToken(tokenWithDifferentKey);

        // then
        assertThat(result).isFalse();
      }
    }
  }
}