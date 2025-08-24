package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
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
      @ModelAttribute FortuneOptionForm fortuneOption,
      @ModelAttribute FortuneResult fortuneResult,
      HttpSession session,
      Model model
  ) {

    session.removeAttribute("birthInfo");

    int currentYear = LocalDate.now().getYear();
    String periodText = fortuneOption.getPeriod().getDisplayName();
    String title = currentYear + "년 " + periodText + " 운세";

    model.addAttribute("fortuneTitle", title);
    model.addAttribute("fortuneResult", fortuneResult);

    return "fortune-result";
  }
}
