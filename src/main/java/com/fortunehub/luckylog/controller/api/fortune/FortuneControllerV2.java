package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/fortunes")
public class FortuneControllerV2 {

  private final FortuneService fortuneService;

  @PostMapping
  public ResponseEntity<Void> save(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody SaveFortuneRequest request
  ) {
    Long savedId = fortuneService.save(userDetails.getMember(), request);

    log.info("[운세 저장 완료] | memberId={}", userDetails.getMember().getId());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedId)
        .toUri();

    return ResponseEntity
        .created(location)
        .build();
  }
}
