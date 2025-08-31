package com.fortunehub.luckylog.controller.web.fortune.form;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BirthInfoForm {

  @NotNull(message = "👶 성별을 선택해주세요!")
  private GenderType gender = GenderType.FEMALE;
  @NotNull(message = "📅 양력 또는 음력을 선택해주세요!")
  private CalendarType calendar = CalendarType.SOLAR;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer year;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer month;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer day;
  private TimeType time;
  private CityType city;
}