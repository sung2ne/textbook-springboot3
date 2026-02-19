// 수정: src/main/java/com/example/board/repository/MemberRepository.java (Spring Security 메서드 추가)
package com.example.board.repository;

import com.example.board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 아이디로 회원 조회 (로그인용)
    Optional<Member> findByUsername(String username);

    // 아이디 중복 체크
    boolean existsByUsername(String username);

    // 이메일로 조회
    Optional<Member> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 이름으로 조회 (PART 03에서 추가)
    Optional<Member> findByName(String name);
}
