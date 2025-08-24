package com.fortunehub.luckylog.domain.fortune;

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
  private final boolean enable;
}
