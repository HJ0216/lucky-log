package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/fortune-option")
public class FortuneOptionController {

  @GetMapping
  public String show(@ModelAttribute BirthInfoForm birthInfo, Model model) {

    model.addAttribute("fortuneOptionForm", new FortuneOptionForm());

    return "fortune-option";
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute BirthInfoForm birthInfoForm,
      @Valid @ModelAttribute FortuneOptionForm fortuneOptionForm,
      BindingResult result,
      Model model
  ) throws Exception {

    log.debug("사용자 정보 상세: 달력={}, 성별={}, 년도={}, 월={}, 일={}, 시간={}, 도시={}",
        birthInfoForm.getCalendar(),
        birthInfoForm.getGender(),
        birthInfoForm.getYear(),
        birthInfoForm.getMonth(),
        birthInfoForm.getDay(),
        birthInfoForm.getTime(),
        birthInfoForm.getCity());

    log.debug("운세 옵션 상세: ai={}, 종류={}, 기간별={}",
        fortuneOptionForm.getAi(),
        fortuneOptionForm.getFortunes(),
        fortuneOptionForm.getPeriod());

    if (result.hasErrors()) {
      Set<String> errorMessages = new LinkedHashSet<>();
      Set<String> errorFields = new LinkedHashSet<>();

      result.getFieldErrors().forEach(error -> {
        errorMessages.add(error.getDefaultMessage());
        errorFields.add(error.getField());
      });

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("errorFields", errorFields);

      return "fortune-option";
    }

    model.addAttribute("birthInfo", birthInfoForm);
    model.addAttribute("fortuneOption", fortuneOptionForm);

    // TODO: ai 연결
    model.addAttribute("fortuneResult", new FortuneResult());

    return "fortune-result";
  }
}
