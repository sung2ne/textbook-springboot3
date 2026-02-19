// 새 파일: src/main/java/com/example/board/service/CommentService.java
package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.domain.Comment;
import com.example.board.domain.Member;
import com.example.board.dto.CommentForm;
import com.example.board.dto.CommentResponse;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 댓글 목록 조회
    public List<CommentResponse> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdWithMember(boardId).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentResponse save(Long boardId, CommentForm form) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 임시: 작성자명으로 회원 조회 또는 생성
        Member member = memberRepository.findByName(form.getWriter())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .name(form.getWriter())
                                .email(form.getWriter() + "@temp.com")
                                .password("temp")
                                .build()
                ));

        Comment comment = Comment.builder()
                .content(form.getContent())
                .board(board)
                .member(member)
                .build();

        Comment saved = commentRepository.save(comment);
        return new CommentResponse(saved);
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        commentRepository.delete(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse update(Long commentId, CommentForm form) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        comment.update(form.getContent());
        return new CommentResponse(comment);
    }
}
