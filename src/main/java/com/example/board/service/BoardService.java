// 수정: src/main/java/com/example/board/service/BoardService.java
package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.domain.Member;
import com.example.board.dto.BoardDetailResponse;
import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardListResponse;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;

    // 게시글 저장 - 04장에서 작성
    @Transactional
    public Long save(BoardForm form) {
        Member member = memberRepository.findByName(form.getWriterName())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .name(form.getWriterName())
                                .email(form.getWriterName() + "@temp.com")
                                .password("temp")
                                .build()
                ));

        Board board = Board.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .member(member)
                .build();

        return boardRepository.save(board).getId();
    }

    // 게시글 저장 (인증된 사용자용, 파일 포함) - 메서드 보안 적용
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Long save(BoardForm form, String username, List<MultipartFile> files) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

        Board board = Board.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .member(member)
                .build();

        Board saved = boardRepository.save(board);

        // 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            attachmentService.saveAll(files, saved);
        }

        return saved.getId();
    }

    // 상세 조회 (조회수 증가) - 05장에서 작성
    @Transactional
    public BoardDetailResponse findById(Long id) {
        Board board = boardRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        board.increaseViewCount();

        return new BoardDetailResponse(board);
    }

    // 상세 조회 (권한 체크용) - 추가
    @PostAuthorize("returnObject.member.username == authentication.name or hasRole('ADMIN')")
    public Board findByIdWithAuth(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
    }

    // 상세 조회 (조회수 증가 없이 - 수정 폼용) - 05장에서 작성
    public BoardDetailResponse findByIdForEdit(Long id) {
        Board board = boardRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        return new BoardDetailResponse(board);
    }

    // 수정용 폼 조회 - 06장에서 작성
    public BoardForm getFormById(Long id) {
        Board board = boardRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
        return BoardForm.from(board);
    }

    // 게시글 수정 - 06장에서 작성
    @Transactional
    public void update(Long id, BoardForm form) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        board.update(form.getTitle(), form.getContent());
    }

    // 게시글 수정 (메서드 보안 + 파일 포함) - 변경
    // 어노테이션이 내부 권한 검증 로직을 대체
    @PreAuthorize("@boardSecurityService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    @Transactional
    public void update(Long id, BoardForm form, String username, List<MultipartFile> files) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        // 08장의 권한 검증 로직이 @PreAuthorize로 대체됨
        board.update(form.getTitle(), form.getContent());

        // 새 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            attachmentService.saveAll(files, board);
        }
    }

    // 게시글 삭제 - 08장에서 작성
    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
        boardRepository.delete(board);
    }

    // 게시글 삭제 (메서드 보안 적용) - 추가
    // 어노테이션이 isAdmin 파라미터 역할을 대체
    @PreAuthorize("@boardSecurityService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    @Transactional
    public void delete(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
        boardRepository.delete(board);
    }

    // 본인 글인지 확인 - 08장에서 작성
    public boolean isOwner(Long boardId, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + boardId));

        return board.getMember() != null &&
               board.getMember().getUsername().equals(username);
    }

    // 목록 조회 (페이징) - 03장에서 작성
    public Page<BoardListResponse> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardListResponse::new);
    }

    // 검색 (제목 + 내용) - 03장에서 작성
    public Page<BoardListResponse> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return findAll(pageable);
        }
        return boardRepository
                .findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(BoardListResponse::new);
    }
}
