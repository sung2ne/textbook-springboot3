// 새 파일: src/main/java/com/example/board/exception/ResourceNotFoundException.java
package com.example.board.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + "을(를) 찾을 수 없습니다. ID: " + id);
    }
}
