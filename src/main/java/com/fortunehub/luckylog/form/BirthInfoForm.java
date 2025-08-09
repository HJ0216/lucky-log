package com.fortunehub.luckylog.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BirthInfoForm {

  private String gender;
  private String calendar;
  private Integer year;
  private Integer month;
  private Integer day;
  private String time;
  private String city;
}