package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/fortune/result")
public class FortuneResultController {

  @GetMapping
  public String show(
      @ModelAttribute BirthInfoForm birthInfo,
      @ModelAttribute FortuneOptionForm fortuneOption,
      @ModelAttribute FortuneResult fortuneResult,
      Model model
  ) {

    log.info("운세 결과 페이지 접근");

    model.addAttribute("fortuneResult", fortuneResult);

    return "fortune-result";
  }
}
