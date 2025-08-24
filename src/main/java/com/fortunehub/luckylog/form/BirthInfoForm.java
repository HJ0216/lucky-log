package com.fortunehub.luckylog.form;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
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
  private String gender = GenderType.FEMALE.toString();
  @NotNull(message = "📅 양력 또는 음력을 선택해주세요!")
  private String calendar = CalendarType.SOLAR.toString();
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer year;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer month;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer day;
  private String time;
  private String city;
}