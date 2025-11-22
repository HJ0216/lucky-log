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
import lombok.Getter;

@Getter
@Entity
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
}
