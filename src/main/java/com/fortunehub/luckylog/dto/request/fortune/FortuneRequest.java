package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import java.util.List;

public record FortuneRequest(
    // 생년월일 정보
    String gender,
    String calendar,
    Integer year,
    Integer month,
    Integer day,
    String time,
    String city,

    // 운세 옵션 정보
    List<String> fortunes,
    String period
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
    return String.join(", ", fortunes);
  }

  public String getBirthDate() {
    return String.format("%d년 %d월 %d일", year, month, day);
  }

  public String getBirthTime() {
    return hasTime() ? time : "모름";
  }

  public String getBirthPlace() {
    return hasCity() ? city : "모름";
  }

  public String getCalendarType() {
    return switch (calendar) {
      case "solar" -> "양력";
      case "lunar" -> "음력(평달)";
      default -> "음력(윤달)";
    };
  }

  public String getGenderInKorean() {
    return "female".equals(gender) ? "여성" : "남성";
  }

  private boolean hasTime() {
    return time != null && !time.isBlank();
  }

  private boolean hasCity() {
    return city != null && !city.isBlank();
  }
}
