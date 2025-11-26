package com.fortunehub.luckylog.domain.fortune;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodValue {

  JANUARY("january", "1월", 1),
  FEBRUARY("february", "2월", 2),
  MARCH("march", "3월", 3),
  APRIL("april", "4월", 4),
  MAY("may", "5월", 5),
  JUNE("june", "6월", 6),
  JULY("july", "7월", 7),
  AUGUST("august", "8월", 8),
  SEPTEMBER("september", "9월", 9),
  OCTOBER("october", "10월", 10),
  NOVEMBER("november", "11월", 11),
  DECEMBER("december", "12월", 12),

  Q1("q1", "1분기", 1),
  Q2("q2", "2분기", 2),
  Q3("q3", "3분기", 3),
  Q4("q4", "4분기", 4),

  YEAR("year", "연간", 1);
  @JsonValue
  private final String jsonKey;
  private final String displayName;
  private final int periodNumber;
}
