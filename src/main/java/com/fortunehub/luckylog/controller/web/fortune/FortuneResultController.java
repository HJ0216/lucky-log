package com.fortunehub.luckylog.controller.web.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
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
    FortuneOptionForm form = (FortuneOptionForm) model.getAttribute("option");
    List<FortuneResponseView> response = (List<FortuneResponseView>) model.getAttribute("response");

    if (form == null || response == null) {
      log.warn("[FortuneResultController] [운세 결과 표시 실패] - [필수 데이터 누락] | option={} | response={}",
          form, response);
      return "redirect:/";
    }

    int currentYear = LocalDate.now().getYear();
    String periodText = form.getPeriod().getDisplayName();
    String title = currentYear + "년 " + periodText + " 운세";

    model.addAttribute("fortuneTitle", title);

    return "fortune/fortune-result";
  }
}
