package com.fortunehub.luckylog.domain.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.domain.common.BaseTimeEntity;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
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

  public static FortuneResult create(
      Member member,
      SaveFortuneRequest request,
      BirthInfoForm birth
  ) {
    validateInputs(member, request, birth);

    FortuneResult result = new FortuneResult();
    result.member = member;
    result.title =
        StringUtils.hasText(request.getTitle()) ? request.getTitle() : generateTitle(request);
    result.gender = birth.getGender();
    result.resultYear = request.getFortuneResultYear();
    result.birthDate = createBirthDate(birth);
    result.birthTimeZone = birth.getTime();
    result.birthRegion = birth.getCity();
    result.aiType = request.getOption().getAi();
    result.periodType = request.getOption().getPeriod();

    return result;
  }

  private static void validateInputs(Member member, SaveFortuneRequest request,
      BirthInfoForm birth) {
    if (member == null) {
      throw new IllegalArgumentException("회원 정보는 필수입니다.");
    }

    if (request == null) {
      throw new IllegalArgumentException("운세 저장 요청 정보는 필수입니다.");
    }
    if (request.getFortuneResultYear() == null) {
      throw new IllegalArgumentException("운세 결과 연도는 필수입니다.");
    }
    if (request.getOption() == null) {
      throw new IllegalArgumentException("운세 옵션 정보는 필수입니다.");
    }
    if (request.getResponses() == null || request.getResponses().isEmpty()) {
      throw new IllegalArgumentException("운세 결과는 필수입니다.");
    }

    if (birth == null) {
      throw new IllegalArgumentException("생년월일 정보는 필수입니다.");
    }
    if (birth.getYear() == null || birth.getMonth() == null || birth.getDay() == null) {
      throw new IllegalArgumentException("생년월일은 필수입니다.");
    }
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
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 생년월일입니다.", e);
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
