package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import com.fortunehub.luckylog.form.BirthInfoForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.Year;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/")
public class IndexController {

  // 모든 요청에서 자동으로 minYear, maxYear가 model에 추가됨
  @ModelAttribute("minYear")
  public int getMinBirthYear() {
    return 1940;
  }

  @ModelAttribute("maxYear")
  public int getMaxBirthYear() {
    return Year.now().getValue();
  }

  @ModelAttribute("genderTypes")
  public List<GenderType> genderTypes() {
    return GenderType.ALL_TYPES;
  }

  @ModelAttribute("calendarTypes")
  public List<CalendarType> calendarTypes() {
    return CalendarType.ALL_TYPES;
  }

  @ModelAttribute("timeTypes")
  public List<TimeType> timeTypes() {
    return TimeType.ALL_TYPES;
  }

  @ModelAttribute("cityTypes")
  public List<CityType> cityTypes() {
    return CityType.ALL_TYPES;
  }

  @GetMapping
  public String index(HttpSession session, Model model) {
    BirthInfoForm birthInfo = (BirthInfoForm) session.getAttribute("birthInfo");
    model.addAttribute("birthInfoForm", birthInfo != null ? birthInfo : new BirthInfoForm());

    return "index"; // templates/index.html 반환
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute BirthInfoForm birthInfoForm,
      BindingResult result,
      HttpSession session
  ) {
    // @ModelAttribute: 자동으로 Model에 추가 (birthInfoForm 이름으로)
    // 사용자 입력값이 그대로 유지됨

    log.debug("생년월일 제출: 생년: {}, 성별: {}, 시간 선택 여부: {}, 장소 선택 여부: {}",
        birthInfoForm.getYear(),
        birthInfoForm.getGender(),
        birthInfoForm.getTime() != null && birthInfoForm.getTime() != TimeType.UNKNOWN,
        birthInfoForm.getCity() != null && birthInfoForm.getCity() != CityType.UNKNOWN);

    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.debug("생년월일 검증 실패: 필드: {}, 입력값: {}, 메시지: {}",
              error.getField(),
              error.getRejectedValue(),
              error.getDefaultMessage())
      );

      return "index";
    }

    // option 페이지에서 뒤로가기 시, 기존 데이터 저장
    session.setAttribute("birthInfo", birthInfoForm);

    return "redirect:/fortune/option";
  }
}
