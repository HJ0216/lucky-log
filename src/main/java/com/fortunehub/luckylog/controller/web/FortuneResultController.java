package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
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
      @ModelAttribute FortuneOptionForm fortuneOptionForm, // 요청 데이터 받기(클라이언트 → 서버)
      @ModelAttribute FortuneResult fortuneResult,
      HttpSession session,
      Model model // 뷰로 데이터 보내기(서버 → 클라이언트)
  ) {

    BirthInfoForm savedBirthInfo = (BirthInfoForm) session.getAttribute("birthInfo");
    session.removeAttribute("birthInfo");

    int currentYear = LocalDate.now().getYear();
    String periodText = fortuneOptionForm.getPeriod().getDisplayName();
    String title = currentYear + "년 " + periodText + " 운세";

    model.addAttribute("birthInfo", savedBirthInfo);
    model.addAttribute("fortuneOption", fortuneOptionForm);
    model.addAttribute("fortuneTitle", title);
    model.addAttribute("fortuneResult", fortuneResult);

    return "fortune-result";
  }
}
