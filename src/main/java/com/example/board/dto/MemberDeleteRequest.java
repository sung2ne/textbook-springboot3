// 새 파일: src/main/java/com/example/board/dto/MemberDeleteRequest.java
package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDeleteRequest {

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    private String reason;
}
