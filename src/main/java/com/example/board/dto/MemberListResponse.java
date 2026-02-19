// 새 파일: src/main/java/com/example/board/dto/MemberListResponse.java
package com.example.board.dto;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberListResponse {

    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final Role role;
    private final boolean enabled;
    private final LocalDateTime createdAt;

    public MemberListResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.enabled = member.isEnabled();
        this.createdAt = member.getCreatedAt();
    }
}
