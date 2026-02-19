// 새 파일: src/main/java/com/example/board/dto/PasswordChangeRequest.java
package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
             message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요")
    private String newPasswordConfirm;

    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
    }
}
