// 수정: src/main/java/com/example/board/service/CommentService.java
package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.domain.Comment;
import com.example.board.domain.Member;
import com.example.board.domain.Role;
import com.example.board.dto.CommentForm;
import com.example.board.dto.CommentResponse;
import com.example.board.dto.MyCommentResponse;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 댓글 목록 조회 (09장에서 작성)
    public List<CommentResponse> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdWithMember(boardId).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    // 댓글 작성 - 비인증 (09장에서 작성)
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

    // 댓글 작성 - 인증 기반 (02장에서 추가)
    @Transactional
    public CommentResponse save(Long boardId, CommentForm form, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(form.getContent())
                .board(board)
                .member(member)
                .build();

        Comment saved = commentRepository.save(comment);
        return new CommentResponse(saved);
    }

    // 댓글 삭제 - 비인증 (09장에서 작성)
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        commentRepository.delete(comment);
    }

    // 댓글 삭제 - 인증 기반 (02장에서 추가)
    @Transactional
    public void delete(Long commentId, String username, Role userRole) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 권한 확인: 본인 또는 관리자
        if (!comment.isWrittenBy(username) && userRole != Role.ADMIN) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 수정 - 비인증 (09장에서 작성)
    @Transactional
    public CommentResponse update(Long commentId, CommentForm form) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        comment.update(form.getContent());
        return new CommentResponse(comment);
    }

    // 댓글 수정 - 인증 기반 (02장에서 추가)
    @Transactional
    public CommentResponse update(Long commentId, CommentForm form, String username, Role userRole) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 권한 확인: 본인 또는 관리자
        if (!comment.isWrittenBy(username) && userRole != Role.ADMIN) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.update(form.getContent());
        return new CommentResponse(comment);
    }

    // 회원의 댓글 목록 - 마이페이지용 (02장에서 추가)
    public Page<CommentResponse> findByMemberId(Long memberId, Pageable pageable) {
        return commentRepository.findByMemberId(memberId, pageable)
                .map(CommentResponse::new);
    }

    // 수정/삭제 권한 확인 (02장에서 추가)
    public boolean canEdit(Long commentId, String username, Role userRole) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElse(null);

        if (comment == null) return false;

        return comment.isWrittenBy(username) || userRole == Role.ADMIN;
    }

    // 회원의 댓글 목록 (게시글 정보 포함, 마이페이지용) - 추가
    public Page<MyCommentResponse> findByMemberIdWithBoard(Long memberId, Pageable pageable) {
        return commentRepository.findByMemberIdWithBoard(memberId, pageable)
                .map(MyCommentResponse::new);
    }
}
