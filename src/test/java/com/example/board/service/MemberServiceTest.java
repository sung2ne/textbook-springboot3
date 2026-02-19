package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional  // 테스트 후 롤백
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공")
    void join_success() {
        // when
        Long memberId = memberService.join("홍길동", "hong@test.com", "1234");

        // then
        Member found = memberRepository.findById(memberId).orElseThrow();
        assertThat(found.getName()).isEqualTo("홍길동");
        assertThat(found.getEmail()).isEqualTo("hong@test.com");
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void join_duplicateEmail() {
        // given
        memberService.join("홍길동", "hong@test.com", "1234");

        // when & then
        assertThatThrownBy(() ->
            memberService.join("김철수", "hong@test.com", "5678")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("이름 수정 - 변경 감지")
    void updateName() {
        // given
        Long memberId = memberService.join("홍길동", "hong@test.com", "1234");

        // when
        memberService.updateName(memberId, "홍길순");

        // then
        Member found = memberService.findById(memberId);
        assertThat(found.getName()).isEqualTo("홍길순");
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 예외")
    void findById_notFound() {
        assertThatThrownBy(() ->
            memberService.findById(999L)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("회원을 찾을 수 없습니다.");
    }
}
