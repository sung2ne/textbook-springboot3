// 새 파일: src/main/java/com/example/board/dto/BoardDto.java
package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private String title;
    private String writer;
    private LocalDateTime createdAt;
}
