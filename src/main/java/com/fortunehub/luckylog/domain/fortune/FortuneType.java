package com.fortunehub.luckylog.domain.fortune;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FortuneType {

  OVERALL("🔮", "종합", "overall", true),
  MONEY("💰", "재물", "money", false),
  LOVE("💕", "애정", "love", false),
  CAREER("💼", "직장 사업", "career", false),
  STUDY("📚", "학업 시험", "study", false),
  LUCK("🍀", "행운", "luck", false),
  FAMILY("🏠", "가정", "family", false),
  HEALTH("💪", "건강", "health", false);

  private final String icon;
  private final String tooltip;
  @JsonValue
  private final String jsonKey;
  private final boolean enabled;

  public static final List<FortuneType> ALL_TYPES = List.of(values());
}
