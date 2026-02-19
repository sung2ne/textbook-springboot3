package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // JPA 관련 설정만 로드
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = memberRepository.save(
                Member.builder()
                        .name("테스터")
                        .email("test@test.com")
                        .password("1234")
                        .build()
        );
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void save() {
        // given
        Board board = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .member(testMember)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getMember().getName()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("제목 검색 테스트")
    void findByTitleContaining() {
        // given
        boardRepository.save(Board.builder()
                .title("스프링 부트 기초")
                .content("내용1")
                .member(testMember)
                .build());

        boardRepository.save(Board.builder()
                .title("스프링 시큐리티")
                .content("내용2")
                .member(testMember)
                .build());

        boardRepository.save(Board.builder()
                .title("JPA 기초")
                .content("내용3")
                .member(testMember)
                .build());

        // when
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Board> result = boardRepository.findByTitleContaining("스프링", pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Board::getTitle)
                .containsExactly("스프링 시큐리티", "스프링 부트 기초");
    }

    @Test
    @DisplayName("Fetch Join 테스트")
    void findByIdWithMember() {
        // given
        Board board = boardRepository.save(
                Board.builder()
                        .title("테스트")
                        .content("내용")
                        .member(testMember)
                        .build()
        );

        // when
        Board found = boardRepository.findByIdWithMember(board.getId()).orElseThrow();

        // then
        assertThat(found.getMember().getName()).isEqualTo("테스터");
    }
}
