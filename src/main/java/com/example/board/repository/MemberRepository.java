package com.example.board.repository;

import com.example.board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);     // PART 02에서 작성
    boolean existsByEmail(String email);            // PART 02에서 작성

    Optional<Member> findByName(String name);       // 추가
}
