package com.fortunehub.luckylog.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FortuneOptionForm {

  @NotNull(message = "🤖 AI를 선택해주세요!")
  private String ai = "🪂 Gemini";

  @NotEmpty(message = "🍀 최소 하나의 운세를 선택해주세요!")
  private List<String> fortunes = new ArrayList<>(List.of("overall"));


  @NotNull(message = "📊 운세 주기를 선택해주세요!")
  private String period = "monthly";
}
