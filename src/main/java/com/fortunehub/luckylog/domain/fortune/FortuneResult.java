package com.fortunehub.luckylog.domain.fortune;

import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import com.fortunehub.luckylog.domain.member.Member;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Table(name = "fortune_result")
public class FortuneResult extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "member_id", // FK 컬럼명
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_fortune_result_member")
  )
  private Member member; // Result에서 Member를 직접 조회하진 않지만, 쿼리 작성을 위해 추가

  @Column(nullable = false, length = 30)
  private String title;

  @Column(nullable = false)
  private int resultYear;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private GenderType gender;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TimeType birthTimeZone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CityType birthRegion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private AIType aiType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PeriodType periodType;

  @Column(nullable = false)
  @ColumnDefault("true")
  private boolean isActive = true;

  @OneToMany(mappedBy = "fortuneResult")
  private List<FortuneResultCategory> categories = new ArrayList<>();

  @OneToMany(mappedBy = "fortuneResult")
  private List<FortuneResultItem> items = new ArrayList<>();

  public void setMember(Member member) {
    this.member = member;
  }

  // 연관관계 편의 메서드
}
