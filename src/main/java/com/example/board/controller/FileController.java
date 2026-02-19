package com.example.board.controller;

import com.example.board.domain.Attachment;
import com.example.board.service.AttachmentService;
import com.example.board.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final AttachmentService attachmentService;
    private final FileService fileService;

    // 파일 다운로드
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Attachment attachment = attachmentService.findById(id);
        Path filePath = fileService.load(attachment.getStoredFilename());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        // 한글 파일명 인코딩
        String encodedFilename = URLEncoder.encode(attachment.getOriginalFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    // 이미지 미리보기
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Long id) throws IOException {
        Attachment attachment = attachmentService.findById(id);

        if (!attachment.isImage()) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }

        Path filePath = fileService.load(attachment.getStoredFilename());
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .body(resource);
    }

    // 첨부파일 삭제 - 추가
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
