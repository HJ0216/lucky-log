package com.fortunehub.luckylog.init;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.repository.fortune.FortuneCategoryRepository;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer {

  private final FortuneCategoryRepository fortuneCategoryRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  @Transactional
  public void init() {
    initFortuneCategories();
    initMembers();
  }

  private void initFortuneCategories() {
    if (fortuneCategoryRepository.count() > 0) {
      return;
    }

    fortuneCategoryRepository.saveAll(List.of(
        FortuneCategory.create(FortuneType.OVERALL),
        FortuneCategory.create(FortuneType.MONEY),
        FortuneCategory.create(FortuneType.LOVE),
        FortuneCategory.create(FortuneType.CAREER),
        FortuneCategory.create(FortuneType.STUDY),
        FortuneCategory.create(FortuneType.LUCK),
        FortuneCategory.create(FortuneType.FAMILY),
        FortuneCategory.create(FortuneType.HEALTH)
    ));
  }

  private void initMembers() {
    if (memberRepository.count() > 0) {
      return;
    }

    memberRepository.saveAll(List.of(
        new Member("lucky@email.com", passwordEncoder.encode("password@123"), "솜사탕 구름"),
        new Member("test@email.com", passwordEncoder.encode("test@123"), "테스트 유저")
    ));
  }
}
