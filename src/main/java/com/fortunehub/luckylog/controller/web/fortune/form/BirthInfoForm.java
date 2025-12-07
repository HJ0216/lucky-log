package com.fortunehub.luckylog.controller.web.fortune.form;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Schema(description = "생년월일 정보")
@NoArgsConstructor
@Getter
@Setter
public class BirthInfoForm implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(description = "성별", example = "FEMALE", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "👶 성별을 선택해주세요!")
  private GenderType gender = GenderType.FEMALE;

  @Schema(description = "달력 종류 (양력/음력)", example = "SOLAR", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "📅 양력 또는 음력을 선택해주세요!")
  private CalendarType calendar = CalendarType.SOLAR;

  @Schema(description = "출생 연도", example = "1995", minimum = "1940", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  @Min(1940)
  private Integer year;

  @Schema(description = "출생 월", example = "2", minimum = "1", maximum = "12", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  @Range(min = 1, max = 12)
  private Integer month;

  @Schema(description = "출생 일", example = "16", minimum = "1", maximum = "31", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "🎂 생년월일을 모두 입력해주세요!")
  @Range(min = 1, max = 31)
  private Integer day;

  @Schema(description = "출생 시간 (선택사항)", example = "TIME_11_30")
  private TimeType time;

  @Schema(description = "출생 도시 (선택사항)", example = "SEOUL")
  private CityType city;
}