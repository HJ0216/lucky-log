package com.fortunehub.luckylog.form;

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
  private String gender = "female";
  @NotNull(message = "📅 양력 또는 음력을 선택해주세요!")
  private String calendar = "solar";
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer year;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer month;
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  private Integer day;
  private String time;
  private String city;
}