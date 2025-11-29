package com.fortunehub.luckylog.repository.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FortuneResultRepository extends JpaRepository<FortuneResult, Long> {

  /**
   * SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FortuneResult f WHERE f.member.id =
   * :memberId AND f.title = :title
   */
  boolean existsByMember_IdAndTitle(Long memberId, String title);

  /**
   * SELECT COUNT(f) FROM FortuneResult f WHERE f.member.id = :memberId AND f.isActive = true
   */
  long countByMember_IdAndIsActiveTrue(Long memberId);

  @Query("SELECT DISTINCT fr FROM FortuneResult fr "
      + "JOIN FETCH fr.categories c "
      + "JOIN FETCH c.fortuneCategory fc "
      + "WHERE fr.member.id = :memberId  "
      + "AND fr.isActive = true "
      + "ORDER BY fr.createdAt DESC")
  List<FortuneResult> findAllByMemberIdAndIsActiveTrue(@Param("memberId") Long memberId);

  /**
   * SELECT fr FROM FortuneResult fr WHERE fr.id = :fortuneId AND fr.member.id = :memberId AND fr.isActive = true
   */
  @EntityGraph(attributePaths = {"categories.fortuneCategory", "items"})
  Optional<FortuneResult> findByIdAndMember_IdAndIsActiveTrue(Long fortuneId, Long memberId);
}
