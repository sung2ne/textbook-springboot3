// 수정: src/main/java/com/example/board/domain/Member.java
package com.example.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;  // 로그인 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;      // 표시 이름

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Member(String username, String password, String name, String email, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role != null ? role : Role.USER;
    }

    public void updateName(String name) {
        this.name = name;
    }

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 정보 수정
    public void updateInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 관리자 생성용 정적 팩토리 메서드
    public static Member createAdmin(String username, String password, String name, String email) {
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .role(Role.ADMIN)
                .build();
    }

    // 역할 변경
    public void changeRole(Role role) {
        this.role = role;
    }

    // 계정 활성화 상태 변경
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
