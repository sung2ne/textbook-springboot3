package com.example.board.dto;

import com.example.board.domain.Attachment;
import lombok.Getter;

@Getter
public class AttachmentResponse {

    private final Long id;
    private final String originalFilename;
    private final Long fileSize;
    private final boolean image;

    public AttachmentResponse(Attachment attachment) {
        this.id = attachment.getId();
        this.originalFilename = attachment.getOriginalFilename();
        this.fileSize = attachment.getFileSize();
        this.image = attachment.isImage();
    }
}
