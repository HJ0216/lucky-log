package com.fortunehub.luckylog.domain.fortune;

import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "fortune_result_item")
public class FortuneResultItem extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "fortune_result_id", // FK 컬럼명
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_fortune_result_item_fortune_result")
  )
  private FortuneResult fortuneResult;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private PeriodValue periodValue;

  @Column(nullable = false, length = 255)
  private String content;

  @Column
  private Integer accuracy;

  public static FortuneResultItem create(FortuneResult result, FortuneResponse response) {
    FortuneResultItem item = new FortuneResultItem();
    item.fortuneResult = result;
    item.periodValue = response.getPeriodValue();
    item.content = response.getResult();
    return item;
  }

  // 연관관계 편의 메서드
  protected void setFortuneResult(FortuneResult result) {
    this.fortuneResult = result;
  }
}
