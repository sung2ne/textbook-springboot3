package com.example.board.service;

import com.example.board.dto.BoardListResponse;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    // 목록 조회 (페이징)
    public Page<BoardListResponse> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardListResponse::new);
    }

    // 검색 (제목 + 내용)
    public Page<BoardListResponse> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return findAll(pageable);
        }
        return boardRepository
                .findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(BoardListResponse::new);
    }
}
