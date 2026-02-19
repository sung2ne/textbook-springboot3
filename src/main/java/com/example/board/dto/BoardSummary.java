// 새 파일: src/main/java/com/example/board/dto/BoardSummary.java
package com.example.board.dto;

// Java 16+ record 사용 (불변 객체, 생성자/getter/equals/hashCode 자동 생성)
public record BoardSummary(Long id, String title, String memberName) {}
