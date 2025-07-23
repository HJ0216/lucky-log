package com.fortunehub.luckylog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final String secretKey;
  private final long tokenValidityInMilliseconds;

  public JwtUtil(@Value("${jwt.secret}") String secretKey,
      @Value("${jwt.token-validity}") long tokenValidity) {
    this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    this.tokenValidityInMilliseconds = tokenValidity;
  }

  public String createToken(String email) {
    Claims claims = Jwts.claims().setSubject(email);

    Date now = new Date();
    Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

    return Jwts.builder()
               .setClaims(claims)
               .setIssuedAt(now)
               .setExpiration(validity)
               .signWith(SignatureAlgorithm.HS256, secretKey)
               .compact();
  }
}
