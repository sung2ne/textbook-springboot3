// 새 파일: src/main/java/com/example/board/service/AdminService.java
package com.example.board.service;

import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final BoardRepository boardRepository;

    // 모든 게시글 삭제 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAllBoards() {
        boardRepository.deleteAll();
    }

    // 특정 게시글 강제 삭제 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void forceDeleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    // 게시글 수 조회 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    public long getBoardCount() {
        return boardRepository.count();
    }
}
