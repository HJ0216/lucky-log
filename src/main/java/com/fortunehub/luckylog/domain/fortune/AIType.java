package com.fortunehub.luckylog.domain.fortune;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AIType {
  CLAUDE("🎆", "Claude", false),
  GEMINI("🪂", "Gemini", true),
  GPT("🚀", "GPT", false);

  private final String icon;
  private final String displayName;
  private final boolean enabled;

  public static final List<AIType> ALL_TYPES = List.of(values());
}
