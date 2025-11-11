package com.fortunehub.luckylog.domain.member;

import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class Member extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(unique = true, length = 20)
  private String nickname;

  @Column(length = 500)
  private String profileImageUrl;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  private boolean isActive = true;

  public Member(String email, String password, String nickname) {
    this.email = normalizeEmail(email);
    this.password = password;
    this.nickname = nickname;
  }

  private String normalizeEmail(String email) {
    return email != null ? email.toLowerCase().trim() : null;
  }
}
