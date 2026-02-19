package com.example.board.service;

import com.example.board.domain.Attachment;
import com.example.board.domain.Board;
import com.example.board.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileService fileService;

    // 파일 저장
    @Transactional
    public Attachment save(MultipartFile file, Board board) {
        String storedFilename = fileService.store(file);

        Attachment attachment = Attachment.builder()
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .board(board)
                .build();

        return attachmentRepository.save(attachment);
    }

    // 여러 파일 저장
    @Transactional
    public void saveAll(List<MultipartFile> files, Board board) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                save(file, board);
            }
        }
    }

    // 첨부파일 조회
    public Attachment findById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));
    }

    // 게시글의 첨부파일 목록
    public List<Attachment> findByBoardId(Long boardId) {
        return attachmentRepository.findByBoardId(boardId);
    }

    // 첨부파일 삭제
    @Transactional
    public void delete(Long id) {
        Attachment attachment = findById(id);
        fileService.delete(attachment.getStoredFilename());
        attachmentRepository.delete(attachment);
    }
}
