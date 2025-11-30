package com.fortunehub.luckylog.controller.web.fortune;

import com.fortunehub.luckylog.dto.response.fortune.MyFortuneDetailResponse;
import com.fortunehub.luckylog.dto.response.fortune.MyFortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/fortune/my")
public class FortuneMyController {

  private static final String FORTUNE_MY_VIEW = "fortune/fortune-my";
  private static final String FORTUNE_MY_DETAIL_VIEW = "fortune/fortune-my-detail";

  private final FortuneService fortuneService;

  @GetMapping
  public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    try {
      List<MyFortuneResponse> myFortunes = fortuneService.getMyFortunes(
          userDetails.getMember().getId());
      model.addAttribute("myFortunes", myFortunes);

      return FORTUNE_MY_VIEW;
    } catch (CustomException e) {
      log.warn("[운세 목록 조회 실패] | message={}", e.getMessage(), e);
      model.addAttribute("errorMessage", "😲 사주 목록을 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요.");
      model.addAttribute("myFortunes", Collections.emptyList());

      return FORTUNE_MY_VIEW;
    } catch (Exception e) {
      log.error("[운세 목록 조회 실패] | message={}", e.getMessage(), e);
      model.addAttribute("errorMessage", "😲 사주 목록을 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요.");
      model.addAttribute("myFortunes", Collections.emptyList());

      return FORTUNE_MY_VIEW;
    }
  }

  @GetMapping("/{id}")
  public String detail(
      @PathVariable(name = "id") Long resultId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    try {
      MyFortuneDetailResponse response =
          fortuneService.getMyFortune(resultId, userDetails.getMember().getId());
      model.addAttribute("myFortune", response);

      return FORTUNE_MY_DETAIL_VIEW;

    } catch (CustomException e) {
      log.error("[운세 목록 세부 내용 조회 실패] | message={}", e.getMessage(), e);
      model.addAttribute("errorMessage", "😲 저장된 사주 목록을 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요.");
      model.addAttribute("myFortune", Collections.emptyList());

      return FORTUNE_MY_DETAIL_VIEW;
    } catch (Exception e) {
      log.error("[운세 목록 세부 내용 조회 실패] | message={}", e.getMessage(), e);
      model.addAttribute("errorMessage", "😲 저장된 사주 목록을 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요.");
      model.addAttribute("myFortune", Collections.emptyList());

      return FORTUNE_MY_DETAIL_VIEW;
    }
  }
}
