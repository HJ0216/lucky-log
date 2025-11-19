package com.fortunehub.luckylog.domain.fortune;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FortuneType {

  OVERALL("🔮", "종합", "overall", true),
  MONEY("💰", "재물", "money", true),
  LOVE("💕", "애정", "love", true),
  CAREER("💼", "직장 사업", "career", true),
  STUDY("📚", "학업 시험", "study", true),
  LUCK("🍀", "행운", "luck", true),
  FAMILY("🏠", "가정", "family", true),
  HEALTH("💪", "건강", "health", true);

  private final String icon;
  private final String tooltip;
  @JsonValue // Enum 상수가 JSON에서 어떻게 표현되어야 하는지를 나타내는 문자열 값
  private final String jsonKey;
  private final boolean enabled;

  public static final List<FortuneType> ALL_TYPES = List.of(values());

  public boolean isOverall() {
    return this == OVERALL;
  }

  public String getDisplayString() {
    return getIcon() + getTooltip() + "운";
  }
}
