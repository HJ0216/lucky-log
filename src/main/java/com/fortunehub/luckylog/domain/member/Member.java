package com.fortunehub.luckylog.domain.member;

import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname")
    }
)public class Member extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @ColumnDefault("'USER'")
  // DDL 생성 시에만 사용
  // @Column(nullable = false)이면 무조건 값을 포함시켜서 default value값이 jpa insert시 동작 x
  private Role role = Role.USER;

  @Column(length = 20)
  private String nickname;

  @Column(length = 500)
  private String profileImageUrl;

  @Column(nullable = false)
  @ColumnDefault("true")
  private boolean isActive = true;

  public Member(String email, String password, String nickname) {
    this.email = normalizeEmail(email);
    this.password = password;
    this.nickname = nickname;
  }

  private String normalizeEmail(String email) {
    return email != null ? email.toLowerCase().trim() : null;
  }

  public static Member from(SignupRequest request, String encodedPassword) {
    return new Member(request.getEmail(), encodedPassword, request.getNickname());
  }
}
