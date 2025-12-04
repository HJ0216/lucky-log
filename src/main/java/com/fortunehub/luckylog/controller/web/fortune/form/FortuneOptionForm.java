package com.fortunehub.luckylog.controller.web.fortune.form;

import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "운세 옵션 정보")
@NoArgsConstructor
@Getter
@Setter
public class FortuneOptionForm {

  @Schema(description = "사용할 AI 모델", example = "GEMINI", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "🤖 AI를 선택해주세요!")
  private AIType ai = AIType.GEMINI;

  @Schema(
      description = "조회할 운세 종류 목록",
      example = "[\"LOVE\", \"HEALTH\", \"MONEY\"]",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotEmpty(message = "🍀 최소 하나의 운세를 선택해주세요!")
  private List<FortuneType> fortunes = new ArrayList<>();

  @Schema(description = "운세 조회 주기", example = "MONTHLY", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "📊 운세 주기를 선택해주세요!")
  private PeriodType period = PeriodType.MONTHLY;

  public String getFortunesAsString() {
    if (fortunes == null || fortunes.isEmpty()) {
      return "";
    }

    return fortunes.stream()
                   .map(FortuneType::getDisplayString)
                   .collect(Collectors.joining(", "));
  }
}
