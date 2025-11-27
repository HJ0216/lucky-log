package com.fortunehub.luckylog.repository.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.dto.response.fortune.MyFortuneResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface FortuneResultRepository extends JpaRepository<FortuneResult, Long> {

  /**
   * SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
   * FROM FortuneResult f
   * WHERE f.member.id = :memberId
   * AND f.title = :title
   */
  boolean existsByMember_IdAndTitle(Long memberId, String title);

  /**
   * SELECT COUNT(f)
   * FROM FortuneResult f
   * WHERE f.member.id = :memberId
   * AND f.isActive = true
   */
  long countByMember_IdAndIsActiveTrue(@Param("memberId") Long memberId);

  /**
   * SELECT *
   * FROM FortuneResult f
   * WHERE f.member.id = :memberId
   * AND f.isActive = true
   */
  List<FortuneResult> findAllByMember_IdAndIsActiveTrue(Long memberId);
}
