// 수정: src/main/java/com/example/board/service/MemberService.java (Spring Security 회원가입 추가)
package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import com.example.board.dto.SignupRequest;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 (PART 02)
    @Transactional
    public Long join(String name, String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        return memberRepository.save(member).getId();
    }

    // Spring Security 회원가입
    @Transactional
    public Long signup(SignupRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
            }
        }

        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        return memberRepository.save(member).getId();
    }

    // 아이디 중복 체크
    public boolean isUsernameDuplicate(String username) {
        return memberRepository.existsByUsername(username);
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 회원 조회
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
    }

    // 아이디로 조회
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
    }

    // 이메일로 조회
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    // 전체 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 이름 수정
    @Transactional
    public void updateName(Long id, String name) {
        Member member = findById(id);
        member.updateName(name);
    }

    // 회원 삭제
    @Transactional
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}
