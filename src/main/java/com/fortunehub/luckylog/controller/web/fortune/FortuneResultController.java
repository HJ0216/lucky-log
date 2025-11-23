package com.fortunehub.luckylog.controller.web.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/fortune/result")
public class FortuneResultController {

  @GetMapping
  public String show(
      Model model // 뷰로 데이터 보내기(서버 → 클라이언트)
  ) {
    int fortuneResultYear = (int) model.getAttribute("fortuneResultYear");
    FortuneOptionForm form = (FortuneOptionForm) model.getAttribute("option");
    List<FortuneResponse> responses = (List<FortuneResponse>) model.getAttribute("responses");

    if (form == null || responses == null || responses.isEmpty()) {
      log.warn("[운세 결과 표시 실패] - [필수 데이터 누락] | option={} | responses={}",
          form, responses);
      return "redirect:/";
    }

    model.addAttribute("fortuneResultYear", fortuneResultYear);
    model.addAttribute("periodDisplayValue", form.getPeriod().getDisplayName());
    model.addAttribute("selectedOptions", form.getFortunesAsString());

    return "fortune/fortune-result";
  }
}
