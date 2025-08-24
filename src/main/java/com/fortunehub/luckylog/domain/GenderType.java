package com.fortunehub.luckylog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderType {
  FEMALE("🙋‍♀️ 여성"), MALE("🙋‍♂️ 남성");

  private final String displayName;
}
