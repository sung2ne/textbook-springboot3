// 새 파일: src/main/java/com/example/board/repository/AttachmentRepository.java
package com.example.board.repository;

import com.example.board.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByBoardId(Long boardId);
}
