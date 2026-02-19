// 새 파일: src/test/java/com/example/board/repository/MemberRepositoryTest.java
package com.example.board.repository;

import com.example.board.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // JPA 관련 설정만 로드 (가벼운 테스트)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 테스트")
    void save() {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .build();

        // when
        Member saved = memberRepository.save(member);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("홍길동");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail() {
        // given
        memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .build());

        // when
        Optional<Member> found = memberRepository.findByEmail("hong@test.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void existsByEmail() {
        // given
        memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .build());

        // when & then
        assertThat(memberRepository.existsByEmail("hong@test.com")).isTrue();
        assertThat(memberRepository.existsByEmail("other@test.com")).isFalse();
    }

    @Test
    @DisplayName("회원 삭제")
    void delete() {
        // given
        Member member = memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .build());

        // when
        memberRepository.delete(member);

        // then
        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }
}
