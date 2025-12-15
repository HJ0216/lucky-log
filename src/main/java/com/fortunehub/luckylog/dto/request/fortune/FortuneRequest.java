package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.common.cache.CacheKeyProvider;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FortuneRequest implements CacheKeyProvider {

  // 생년월일 정보
  private final GenderType gender;
  private final CalendarType calendar;
  private final Integer year;
  private final Integer month;
  private final Integer day;
  private final TimeType time;
  private final CityType city;

  // 운세 옵션 정보
  private final List<FortuneType> fortunes;
  private final PeriodType period;

  // 운세 결과 연도
  private final Integer fortuneResultYear;

  public static FortuneRequest from(
      BirthInfoForm birthInfo,
      FortuneOptionForm fortuneOption,
      int fortuneResultYear) {
    return new FortuneRequest(
        birthInfo.getGender(),
        birthInfo.getCalendar(),
        birthInfo.getYear(),
        birthInfo.getMonth(),
        birthInfo.getDay(),
        birthInfo.getTime(),
        birthInfo.getCity(),
        fortuneOption.getFortunes(),
        fortuneOption.getPeriod(),
        fortuneResultYear
    );
  }

  public String getFortuneTypesAsString() {
    return fortunes.stream()
                   .map(FortuneType::getTooltip)
                   .collect(Collectors.joining(", "));
  }

  public String getBirthTime() {
    return time == null ? "모름" : time.getDisplayName();
  }

  public String getBirthCity() {
    return city == null ? "모름" : city.getDisplayName();
  }

  public String getBirthDate() {
    return String.format("%d년 %d월 %d일", year, month, day);
  }

  public List<FortuneType> getFortunes() {
    return fortunes;
  }

  public String toBirthInfo() {
    return new StringBuilder("# 사용자 정보")
        .append("\n- 생년월일: ").append(getBirthDate())
        .append("\n- 양력/음력: ").append(calendar.getDisplayName())
        .append("\n- 성별: ").append(gender.getDisplayName())
        .append("\n- 출생시간: ").append(getBirthTime())
        .append("\n- 출생장소: ").append(getBirthCity())
        .toString();
  }

  @Override
  public String cacheKey() {
    String timeKey = (time == null) ? TimeType.UNKNOWN.name() : time.name();
    String cityKey = (city == null) ? CityType.UNKNOWN.name() : city.name();
    String fortuneKeys = fortunes.stream()
                                 .map(Enum::name)
                                 .sorted()
                                 .collect(Collectors.joining(","));

    return String.join(":",
        gender.name(),
        calendar.name(),
        year.toString(),
        month.toString(),
        day.toString(),
        timeKey,
        cityKey,
        fortuneKeys,
        period.name(),
        fortuneResultYear.toString()
    );
  }
}
