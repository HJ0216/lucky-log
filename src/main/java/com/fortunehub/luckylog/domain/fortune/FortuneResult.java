package com.fortunehub.luckylog.domain.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import jakarta.persistence.CascadeType;
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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.util.StringUtils;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @Column(nullable = false, length = 100)
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

  @OneToMany(mappedBy = "fortuneResult",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<FortuneResultCategory> categories = new ArrayList<>();

  @OneToMany(mappedBy = "fortuneResult",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<FortuneResultItem> items = new ArrayList<>();

  public static FortuneResult create(Member member, SaveFortuneRequest request) {
    validateInputs(member, request);

    FortuneResult result = new FortuneResult();
    result.member = member;
    result.title = getOrGenerateTitle(request);
    result.resultYear = request.getFortuneResultYear();

    BirthInfoForm birthInfo = request.getBirthInfo();
    result.gender = birthInfo.getGender();
    result.birthDate = createBirthDate(birthInfo);
    result.birthTimeZone = birthInfo.getTime();
    result.birthRegion = birthInfo.getCity();

    FortuneOptionForm option = request.getOption();
    result.aiType = option.getAi();
    result.periodType = option.getPeriod();

    return result;
  }

  private static void validateInputs(Member member, SaveFortuneRequest request) {
    if (member == null) {
      throw new CustomException(ErrorCode.MEMBER_INFO_REQUIRED);
    }

    if (request == null) {
      throw new CustomException(ErrorCode.FORTUNE_REQUEST_REQUIRED);
    }
    if (request.getBirthInfo() == null) {
      throw new CustomException(ErrorCode.BIRTH_INFO_REQUIRED);
    }
    if (request.getOption() == null) {
      throw new CustomException(ErrorCode.FORTUNE_OPTION_REQUIRED);
    }
  }

  private static String getOrGenerateTitle(SaveFortuneRequest request) {
    if (StringUtils.hasText(request.getTitle())) {
      return request.getTitle();
    }
    return generateTitle(request);
  }

  private static String generateTitle(SaveFortuneRequest request) {
    return String.format("%d년 %s %s",
        request.getFortuneResultYear(),
        request.getOption().getPeriod().getDisplayName(),
        request.getOption().getFortunesAsString()
    );
  }

  private static LocalDate createBirthDate(BirthInfoForm birth) {
    try {
      return LocalDate.of(birth.getYear(), birth.getMonth(), birth.getDay());
    } catch (DateTimeException e) {
      throw new CustomException(ErrorCode.INVALID_BIRTH_DATE, e);
    }
  }

  // 연관관계 편의 메서드
  public void addCategory(FortuneResultCategory category) {
    this.categories.add(category);
    category.setFortuneResult(this);
  }

  public void addItem(FortuneResultItem item) {
    this.items.add(item);
    item.setFortuneResult(this);
  }
}
