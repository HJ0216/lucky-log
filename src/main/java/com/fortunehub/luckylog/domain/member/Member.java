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

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = true, unique = true, length = 20)
  private String nickname;

  @Column(nullable = false)
  private String password;

  @Column(length = 500)
  private String profileImageUrl;

  @Column(nullable = false)
  private boolean isActive;

  public Member(String email, String nickname, String password) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.isActive = true;
  }

  // 메서드
  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updateProfileImage(String url) {
    this.profileImageUrl = url;
  }

  public void updateIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void updatePassword(String updatedPassword) {
    this.password = updatedPassword;
  }
}
