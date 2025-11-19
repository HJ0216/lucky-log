package com.fortunehub.luckylog.controller.web.fortune.form;

import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FortuneOptionForm {

  @NotNull(message = "🤖 AI를 선택해주세요!")
  private AIType ai = AIType.GEMINI;

  @NotEmpty(message = "🍀 최소 하나의 운세를 선택해주세요!")
  private List<FortuneType> fortunes = new ArrayList<>();

  @NotNull(message = "📊 운세 주기를 선택해주세요!")
  private PeriodType period = PeriodType.MONTHLY;

  public String getFortunesAsString() {
    return fortunes.stream()
                   .map(type -> type.getIcon() + type.getTooltip() + "운")
                   .collect(Collectors.joining(", "));
  }
}
