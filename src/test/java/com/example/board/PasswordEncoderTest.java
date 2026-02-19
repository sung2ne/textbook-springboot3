// 새 파일: src/test/java/com/example/board/PasswordEncoderTest.java
package com.example.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("비밀번호가 암호화된다")
    void encodePassword() {
        // given
        String rawPassword = "password123!";

        // when
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // then
        System.out.println("평문: " + rawPassword);
        System.out.println("암호화: " + encodedPassword);

        // 1. 암호화된 비밀번호는 평문과 다르다
        assertThat(encodedPassword).isNotEqualTo(rawPassword);

        // 2. BCrypt 형식으로 시작한다 ($2a$ 또는 $2b$)
        assertThat(encodedPassword).startsWith("$2");

        // 3. 60자 길이이다
        assertThat(encodedPassword).hasSize(60);
    }

    @Test
    @DisplayName("같은 비밀번호도 매번 다른 해시값이 생성된다")
    void differentHashForSamePassword() {
        // given
        String rawPassword = "password123!";

        // when
        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);
        String hash3 = passwordEncoder.encode(rawPassword);

        // then
        System.out.println("해시 1: " + hash1);
        System.out.println("해시 2: " + hash2);
        System.out.println("해시 3: " + hash3);

        // 모든 해시값이 서로 다르다
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(hash2).isNotEqualTo(hash3);
        assertThat(hash1).isNotEqualTo(hash3);
    }

    @Test
    @DisplayName("matches()로 비밀번호를 검증할 수 있다")
    void matchesPassword() {
        // given
        String rawPassword = "password123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when & then
        // 올바른 비밀번호는 일치한다
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();

        // 잘못된 비밀번호는 일치하지 않는다
        assertThat(passwordEncoder.matches("wrongpassword", encodedPassword)).isFalse();
        assertThat(passwordEncoder.matches("password123", encodedPassword)).isFalse();
        assertThat(passwordEncoder.matches("Password123!", encodedPassword)).isFalse();
    }

    @Test
    @DisplayName("다른 해시값이어도 같은 비밀번호면 검증에 성공한다")
    void matchesWithDifferentHashes() {
        // given
        String rawPassword = "password123!";
        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);

        // when & then
        // 해시값은 다르지만
        assertThat(hash1).isNotEqualTo(hash2);

        // 둘 다 같은 비밀번호로 검증 성공
        assertThat(passwordEncoder.matches(rawPassword, hash1)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, hash2)).isTrue();
    }
}
