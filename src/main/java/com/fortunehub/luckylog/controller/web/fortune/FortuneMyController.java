package com.fortunehub.luckylog.controller.web.fortune;

import com.fortunehub.luckylog.dto.response.fortune.MyFortuneResponse;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/fortune/my")
public class FortuneMyController {

  private static final String FORTUNE_MY_VIEW = "fortune/fortune-my";

  private final FortuneService fortuneService;

  @GetMapping
  public String show(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    List<MyFortuneResponse> myFortunes = fortuneService.getMyFortunes(userDetails.getMember().getId());
    model.addAttribute("myFortunes", myFortunes);
    return FORTUNE_MY_VIEW;
  }

}
