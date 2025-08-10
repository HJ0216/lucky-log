package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import jakarta.validation.Valid;
import java.time.Year;
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
@RequestMapping("/")
public class IndexController {

  private static final int MIN_BIRTH_YEAR = 1940;

  @GetMapping
  public String index(Model model) {
    // Model: Controller에서 생성된 데이터를 담아 View로 전달할 때 사용하는 객체

    // 빈 BirthInfoForm 객체 생성
    model.addAttribute("birthInfoForm", new BirthInfoForm());

    // 년도 범위 설정
    model.addAttribute("minYear", MIN_BIRTH_YEAR);
    model.addAttribute("maxYear", Year.now().getValue());

    return "index"; // templates/index.html 반환
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute BirthInfoForm birthInfoForm,
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

    if (result.hasErrors()) {
      Set<String> errorMessages = new LinkedHashSet<>();
      Set<String> errorFields = new LinkedHashSet<>();

      result.getFieldErrors().forEach(error -> {
        errorMessages.add(error.getDefaultMessage());
        errorFields.add(error.getField());
      });

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("errorFields", errorFields);

      return "index";
    }

    model.addAttribute("birthInfo", birthInfoForm);
    model.addAttribute("fortuneOptionForm", new FortuneOptionForm());

    return "fortune-option";
  }
}
