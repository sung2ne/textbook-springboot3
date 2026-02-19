// 수정: src/test/java/com/example/board/repository/BoardRepositoryTest.java
package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    @Test
    @DisplayName("연관관계 설정 - 게시글에 작성자 연결")
    void createBoardWithMember() {
        // given
        Board board = Board.builder()
                .title("테스트 게시글")
                .content("연관관계 테스트")
                .member(savedMember)  // Member 연결
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember()).isNotNull();
        assertThat(saved.getMember().getName()).isEqualTo("홍길동");
        assertThat(saved.getWriterName()).isEqualTo("홍길동");  // 편의 메서드 테스트
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
        // given - member 없이 게시글 생성
        Board board = Board.builder()
                .title("익명 게시글")
                .content("작성자 없음")
                .member(null)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getMember()).isNull();
        assertThat(saved.getWriterName()).isEqualTo("익명");  // null 처리 확인
    }
}
