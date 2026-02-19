// 새 파일: src/main/java/com/example/board/security/BoardSecurityService.java
package com.example.board.security;

import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardSecurityService {

    private final BoardRepository boardRepository;

    public boolean isOwner(Long boardId, String username) {
        return boardRepository.findById(boardId)
            .map(board -> board.getMember().getUsername().equals(username))
            .orElse(false);
    }

    public boolean canEdit(Long boardId, String username) {
        return isOwner(boardId, username);
    }

    public boolean canDelete(Long boardId, String username) {
        return isOwner(boardId, username);
    }
}
