package com.fortunehub.luckylog.domain.fortune;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FortuneType {

  OVERALL("🔮", "종합", true),
  MONEY("💰", "재물", false),
  LOVE("💕", "애정", false),
  CAREER("💼", "직장 사업", false),
  STUDY("📚", "학업 시험", false),
  LUCK("🍀", "행운", false),
  FAMILY("🏠", "가정", false),
  HEALTH("💪", "건강", false);

  private final String icon;
  private final String tooltip;
  private final boolean enabled;
}
