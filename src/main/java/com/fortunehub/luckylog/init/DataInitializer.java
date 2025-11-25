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
        FortuneCategory.create(1, FortuneType.OVERALL),
        FortuneCategory.create(2, FortuneType.MONEY),
        FortuneCategory.create(3, FortuneType.LOVE),
        FortuneCategory.create(4, FortuneType.CAREER),
        FortuneCategory.create(5, FortuneType.STUDY),
        FortuneCategory.create(6, FortuneType.LUCK),
        FortuneCategory.create(7, FortuneType.FAMILY),
        FortuneCategory.create(8, FortuneType.HEALTH)
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
