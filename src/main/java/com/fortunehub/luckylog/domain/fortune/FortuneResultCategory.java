package com.fortunehub.luckylog.domain.fortune;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fortune_result_category")
public class FortuneResultCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "fortune_result_id", // FK 컬럼명
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_fortune_result_category_fortune_result")
  )
  private FortuneResult fortuneResult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "fortune_category_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_fortune_result_category_fortune_category")
  )
  private FortuneCategory fortuneCategory;

  public static FortuneResultCategory create(FortuneResult result, FortuneCategory category) {
    FortuneResultCategory fortuneResultCategory = new FortuneResultCategory();
    fortuneResultCategory.fortuneResult = result;
    fortuneResultCategory.fortuneCategory = category;
    return fortuneResultCategory;
  }

  // 연관관계 편의 메서드
  protected void setFortuneResult(FortuneResult result) {
    this.fortuneResult = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FortuneResultCategory)) return false;
    FortuneResultCategory that = (FortuneResultCategory) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
