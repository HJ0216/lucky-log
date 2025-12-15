package com.fortunehub.luckylog.service.fortune;

import com.fortunehub.luckylog.client.gemini.GeminiService;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.dto.response.fortune.MyFortuneDetailResponse;
import com.fortunehub.luckylog.dto.response.fortune.MyFortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.fortune.FortuneCategoryRepository;
import com.fortunehub.luckylog.repository.fortune.FortuneResultRepository;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FortuneService {

  private static final int MAX_SAVE_COUNT = 5;

  private final MemberRepository memberRepository;
  private final GeminiService geminiService;
  private final FortuneResultRepository fortuneResultRepository;
  private final FortuneCategoryRepository fortuneCategoryRepository;

  public List<FortuneResponse> generateFortune(
      String sessionId, BirthInfoForm savedBirthInfo, FortuneOptionForm option, int fortuneResultYear) {
    return switch (option.getAi()) {
      case GEMINI -> geminiService.generateFortune(
          FortuneRequest.from(sessionId, savedBirthInfo, option, fortuneResultYear));
      default -> throw new CustomException(ErrorCode.UNSUPPORTED_AI_TYPE);
    };
  }

  @Transactional
  public Long save(Long memberId, SaveFortuneRequest request) {

    Member member = memberRepository.findById(memberId)
                                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER));

    validateBusinessRules(member, request);

    FortuneResult result = FortuneResult.create(member, request);

    addItems(result, request.getResponses());
    addCategories(result, request.getOption().getFortunes());

    FortuneResult saved = fortuneResultRepository.save(result);
    return saved.getId();
  }

  private void addItems(FortuneResult result, List<FortuneResponse> responses) {
    responses.forEach(response -> {
      FortuneResultItem item = FortuneResultItem.create(response);
      result.addItem(item);
    });
  }

  private void addCategories(FortuneResult result, List<FortuneType> fortuneTypes) {
    List<FortuneCategory> categories = fortuneCategoryRepository
        .findByFortuneTypeIn(fortuneTypes);

    Set<FortuneType> requested = new HashSet<>(fortuneTypes);
    Set<FortuneType> found = categories.stream()
                                       .map(FortuneCategory::getFortuneType)
                                       .collect(Collectors.toSet());

    if (!found.equals(requested)) {
      throw new CustomException(ErrorCode.FORTUNE_CATEGORY_NOT_FOUND);
    }

    categories.forEach(category -> {
      FortuneResultCategory fortuneResultCategory = FortuneResultCategory.create(result, category);
      result.addCategory(fortuneResultCategory);
    });
  }

  private void validateBusinessRules(Member member, SaveFortuneRequest request) {
    if (!member.isActive()) {
      throw new CustomException(ErrorCode.INVALID_MEMBER);
    }

    if (isDuplicateFortuneTitle(member.getId(), request)) {
      throw new CustomException(ErrorCode.DUPLICATE_FORTUNE_TITLE);
    }

    if (isExceedMaxSaveCount(member.getId())) {
      throw new CustomException(ErrorCode.EXCEED_MAX_SAVE_COUNT);
    }
  }

  private boolean isDuplicateFortuneTitle(Long memberId, SaveFortuneRequest request) {
    return fortuneResultRepository.existsByMember_IdAndTitle(memberId, request.getTitle());
  }

  private boolean isExceedMaxSaveCount(Long memberId) {
    long count = fortuneResultRepository.countByMember_IdAndIsActiveTrue(memberId);
    return count >= MAX_SAVE_COUNT;
  }

  public List<MyFortuneResponse> getMyFortunes(Long memberId) {
    if (!isValidId(memberId)) {
      throw new CustomException(ErrorCode.INVALID_MEMBER);
    }

    return fortuneResultRepository.findAllByMember_IdAndIsActiveTrue(memberId).stream()
                                  .map(MyFortuneResponse::from)
                                  .toList();
  }

  private boolean isValidId(Long id) {
    return id != null && id > 0;
  }

  public MyFortuneDetailResponse getMyFortune(Long fortuneId, Long memberId) {
    if (!isValidId(fortuneId)) {
      throw new CustomException(ErrorCode.INVALID_FORTUNE);
    }

    if (!isValidId(memberId)) {
      throw new CustomException(ErrorCode.INVALID_MEMBER);
    }

    FortuneResult fortune = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(fortuneId, memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_FORTUNE));

    return MyFortuneDetailResponse.from(fortune);
  }
}
