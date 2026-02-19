// 새 파일: src/main/java/com/example/board/validation/PasswordMatchingValidator.java
package com.example.board.validation;

import com.example.board.dto.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, SignupRequest> {

    @Override
    public boolean isValid(SignupRequest signupRequest, ConstraintValidatorContext context) {
        if (signupRequest == null) {
            return true;
        }

        return signupRequest.isPasswordMatching();
    }
}
