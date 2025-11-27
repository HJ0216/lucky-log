package com.fortunehub.luckylog.domain.fortune;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AIType {
  CLAUDE("🎆", "Claude", "클로드", false),
  GEMINI("🪂", "Gemini", "잼미니",true),
  GPT("🚀", "GPT", "채찍피티",false);

  private final String icon;
  private final String displayName;
  private final String nickname;
  private final boolean enabled;

  public static final List<AIType> ALL_TYPES = List.of(values());
}
