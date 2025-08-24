package com.fortunehub.luckylog.domain.fortune;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodType {

  MONTHLY("📆", "월별", true),
  QUARTERLY("📊", "분기별", false),
  YEARLY("📋", "올 한해", false);

  private final String icon;
  private final String displayName;
  private final boolean enabled;

  public static final List<PeriodType> ALL_TYPES = List.of(values());
}
