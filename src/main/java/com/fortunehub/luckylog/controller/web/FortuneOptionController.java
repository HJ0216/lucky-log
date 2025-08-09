package com.fortunehub.luckylog.controller.web;

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
@RequestMapping("/fortune-option")
public class FortuneOptionController {

  @GetMapping
  public String show(@ModelAttribute BirthInfoForm birthInfo, Model model) {

    model.addAttribute("fortuneOptionsForm", new FortuneOptionForm());

    return "fortune-option";
  }
}
