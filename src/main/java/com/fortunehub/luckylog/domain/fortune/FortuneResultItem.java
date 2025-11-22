package com.fortunehub.luckylog.domain.fortune;

import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
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

  @Column(nullable = false)
  private int periodValue;

  @Column(nullable = false, length = 255)
  private String content;

  @Column
  private int accuracy;
}
