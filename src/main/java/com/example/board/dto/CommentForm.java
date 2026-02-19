// 수정: src/main/java/com/example/board/dto/CommentForm.java
package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentForm {

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 500, message = "댓글은 500자 이내로 입력해주세요.")
    private String content;

    // PART 03용 (선택 입력, PART 05에서는 서버가 인증 정보로 자동 설정)
    private String writer;
}
