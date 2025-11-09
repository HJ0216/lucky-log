package com.fortunehub.luckylog.repository.member;

import com.fortunehub.luckylog.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  // select 1 from member where email=:email
  boolean existsByEmail(String email);
}
