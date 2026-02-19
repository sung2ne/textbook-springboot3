// 수정: src/test/java/com/example/board/repository/BoardRepositoryTest.java
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        savedMember = memberRepository.save(
            Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .build()
        );
    }

    // ===== 02장: 연관관계 테스트 =====

    @Test
    @DisplayName("연관관계 설정 - 게시글에 작성자 연결")
    void createBoardWithMember() {
        // given
        Board board = Board.builder()
                .title("테스트 게시글")
                .content("연관관계 테스트")
                .member(savedMember)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember()).isNotNull();
        assertThat(saved.getMember().getName()).isEqualTo("홍길동");
        assertThat(saved.getWriterName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("Fetch Join - 게시글과 작성자 함께 조회")
    void findByIdWithMember() {
        // given
        Board board = boardRepository.save(
            Board.builder()
                .title("Fetch Join 테스트")
                .content("N+1 문제 해결")
                .member(savedMember)
                .build()
        );

        // when
        Board found = boardRepository.findByIdWithMember(board.getId())
                .orElseThrow();

        // then
        assertThat(found.getTitle()).isEqualTo("Fetch Join 테스트");
        assertThat(found.getMember().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("Fetch Join - 목록 조회")
    void findAllWithMember() {
        // given
        boardRepository.save(Board.builder()
                .title("게시글 1")
                .content("내용 1")
                .member(savedMember)
                .build());

        boardRepository.save(Board.builder()
                .title("게시글 2")
                .content("내용 2")
                .member(savedMember)
                .build());

        // when
        List<Board> boards = boardRepository.findAllWithMember();

        // then
        assertThat(boards).hasSize(2);
        assertThat(boards.get(0).getMember().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("작성자 없는 게시글 - getWriterName() 익명 반환")
    void boardWithoutMember() {
        // given
        Board board = Board.builder()
                .title("익명 게시글")
                .content("작성자 없음")
                .member(null)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getMember()).isNull();
        assertThat(saved.getWriterName()).isEqualTo("익명");
    }

    // ===== 03. 페이징 테스트 =====

    @Test
    @DisplayName("페이징 조회 - 첫 페이지")
    void findAllWithPaging() {
        // given - 테스트 데이터 15개 생성
        for (int i = 1; i <= 15; i++) {
            boardRepository.save(Board.builder()
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .member(savedMember)
                    .build());
        }

        // when - 첫 페이지, 10개씩
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Board> page = boardRepository.findAll(pageable);

        // then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("페이징 조회 - 마지막 페이지")
    void findAllWithPagingLastPage() {
        // given - 테스트 데이터 15개 생성
        for (int i = 1; i <= 15; i++) {
            boardRepository.save(Board.builder()
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .member(savedMember)
                    .build());
        }

        // when - 두 번째 페이지 (마지막)
        Pageable pageable = PageRequest.of(1, 10, Sort.by("id").descending());
        Page<Board> page = boardRepository.findAll(pageable);

        // then
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.isLast()).isTrue();
        assertThat(page.hasNext()).isFalse();
    }

    // ===== 04. 검색 테스트 (이 페이지에서 추가) =====

    @Test
    @DisplayName("제목 검색 + 페이징")
    void findByTitleContainingWithPaging() {
        // given - 테스트 데이터 생성
        for (int i = 1; i <= 5; i++) {
            boardRepository.save(Board.builder()
                    .title("JPA 게시글 " + i)
                    .content("내용 " + i)
                    .member(savedMember)
                    .build());
        }
        for (int i = 1; i <= 5; i++) {
            boardRepository.save(Board.builder()
                    .title("Spring 게시글 " + i)
                    .content("내용 " + i)
                    .member(savedMember)
                    .build());
        }

        // when - "JPA" 키워드로 검색
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Board> page = boardRepository.findByTitleContaining("JPA", pageable);

        // then
        assertThat(page.getContent()).hasSize(5);        // JPA 게시글만 조회
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent())
                .allMatch(board -> board.getTitle().contains("JPA"));
    }

    @Test
    @DisplayName("제목 또는 내용 검색 (OR 조건)")
    void findByTitleOrContentContaining() {
        // given
        boardRepository.save(Board.builder()
                .title("JPA 기초")
                .content("일반 내용")
                .member(savedMember)
                .build());
        boardRepository.save(Board.builder()
                .title("일반 제목")
                .content("JPA 고급 내용")
                .member(savedMember)
                .build());
        boardRepository.save(Board.builder()
                .title("Spring 게시글")
                .content("Spring 내용")
                .member(savedMember)
                .build());

        // when - 제목 또는 내용에서 "JPA" 검색
        Pageable pageable = PageRequest.of(0, 10);
        Page<Board> page = boardRepository.findByTitleContainingOrContentContaining(
                "JPA", "JPA", pageable);

        // then
        assertThat(page.getContent()).hasSize(2);  // 제목 또는 내용에 JPA 포함
    }

    @Test
    @DisplayName("통합 검색 - null이면 전체 조회")
    void searchWithNullKeyword() {
        // given
        for (int i = 1; i <= 15; i++) {
            boardRepository.save(Board.builder()
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .member(savedMember)
                    .build());
        }

        // when - keyword null로 검색
        Pageable pageable = PageRequest.of(0, 10);
        Page<Board> page = boardRepository.search(null, pageable);

        // then
        assertThat(page.getContent()).hasSize(10);         // 첫 페이지 10개
        assertThat(page.getTotalElements()).isEqualTo(15); // 전체 15개
    }
}
