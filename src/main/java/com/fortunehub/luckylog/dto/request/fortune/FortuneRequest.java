package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import com.fortunehub.luckylog.controller.web.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.form.FortuneOptionForm;
import java.util.List;
import java.util.stream.Collectors;

public record FortuneRequest(
    // 생년월일 정보
    GenderType gender,
    CalendarType calendar,
    Integer year,
    Integer month,
    Integer day,
    TimeType time,
    CityType city,

    // 운세 옵션 정보
    List<FortuneType> fortunes,
    PeriodType period
) {

  public static FortuneRequest from(BirthInfoForm birthInfo, FortuneOptionForm fortuneOption) {
    return new FortuneRequest(
        birthInfo.getGender(),
        birthInfo.getCalendar(),
        birthInfo.getYear(),
        birthInfo.getMonth(),
        birthInfo.getDay(),
        birthInfo.getTime(),
        birthInfo.getCity(),
        fortuneOption.getFortunes(),
        fortuneOption.getPeriod()
    );
  }

  public String getFortuneTypesAsString() {
    return fortunes.stream()
                   .map(FortuneType::getTooltip)
                   .collect(Collectors.joining(", "));  }

  public String getBirthTime(){
    return time == null ? "모름" : time.getDisplayName();
  }

  public String getBirthCity(){
    return city == null ? "모름" : city.getDisplayName();
  }

  public String getBirthDate() {
    return String.format("%d년 %d월 %d일", year, month, day);
  }

  public List<FortuneType> getFortunes(){
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
}
