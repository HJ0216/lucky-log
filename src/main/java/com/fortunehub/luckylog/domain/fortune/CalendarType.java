package com.fortunehub.luckylog.domain.fortune;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarType {

  SOLAR("양력"), LUNAR("음력(평달)"), LUNAR_LEAP("음력(윤달)");
  
  private final String displayName;
}
