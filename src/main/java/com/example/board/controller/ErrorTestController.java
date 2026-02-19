// 새 파일: src/main/java/com/example/board/controller/ErrorTestController.java
package com.example.board.controller;

import com.example.board.exception.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test/error")
public class ErrorTestController {

    @GetMapping("/403")
    @PreAuthorize("hasRole('ADMIN')")  // 일반 사용자는 접근 불가
    public String test403() {
        return "test";
    }

    @GetMapping("/404")
    public String test404() {
        throw new ResourceNotFoundException("테스트 리소스를 찾을 수 없습니다");
    }

    @GetMapping("/500")
    public String test500() {
        throw new RuntimeException("테스트 예외 발생");
    }
}
