// 새 파일: src/main/java/com/example/board/dto/BoardSummaryProjection.java
package com.example.board.dto;

// 프로젝션 인터페이스 - getter만 정의
public interface BoardSummaryProjection {
    Long getId();
    String getTitle();
    String getMemberName();  // SELECT 별칭과 일치해야 함
}
