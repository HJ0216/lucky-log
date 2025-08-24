package com.fortunehub.luckylog.domain.fortune;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderType {
  FEMALE("🙋‍♀️", "여성"),
  MALE("🙋‍♂️", "남성");

  private final String icon;
  private final String displayName;

  public static final List<GenderType> ALL_TYPES = List.of(values());
}
