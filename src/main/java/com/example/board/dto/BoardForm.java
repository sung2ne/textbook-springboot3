// 수정: src/main/java/com/example/board/dto/BoardForm.java (전체 교체)
package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardForm {

    private Long id;  // 수정 시 사용 (신규 등록 시 null)

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    // 수정 시 disabled로 전송 안됨 → @NotBlank 대신 Controller에서 검증
    @Size(max = 50, message = "작성자명은 50자 이내로 입력해주세요.")
    private String writerName;  // Entity 필드명과 일치
}
