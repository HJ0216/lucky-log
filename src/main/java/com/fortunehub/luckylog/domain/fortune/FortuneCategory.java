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
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FortuneType fortuneType;

  // 이 엔티티는 초기 데이터 전용이며, 런타임에 임의 생성하지 않음
  public static FortuneCategory create(int sequence, FortuneType fortuneType) {
    FortuneCategory category = new FortuneCategory();
    category.id = sequence;
    category.fortuneType = fortuneType;
    return category;
  }
}
