package com.fortunehub.luckylog.domain.fortune;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
@Table(name = "fortune_category")
public class FortuneCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FortuneType fortuneType;

  public static FortuneCategory create(FortuneType fortuneType) {
    FortuneCategory category = new FortuneCategory();
    category.fortuneType = fortuneType;
    return category;
  }
}
