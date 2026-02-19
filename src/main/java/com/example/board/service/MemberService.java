// 수정: src/main/java/com/example/board/service/MemberService.java
package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import com.example.board.dto.MemberListResponse;
import com.example.board.dto.MemberDeleteRequest;
import com.example.board.dto.PasswordChangeRequest;
import com.example.board.dto.ProfileEditRequest;
import com.example.board.dto.SignupRequest;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

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

    // 관리자용: 회원 목록 조회
    public Page<MemberListResponse> findAllMembers(String keyword, Role role, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty() && role != null) {
            return memberRepository.findByKeywordAndRole(keyword, role, pageable)
                    .map(MemberListResponse::new);
        } else if (keyword != null && !keyword.isEmpty()) {
            return memberRepository.findByKeyword(keyword, pageable)
                    .map(MemberListResponse::new);
        } else if (role != null) {
            return memberRepository.findByRole(role, pageable)
                    .map(MemberListResponse::new);
        }
        return memberRepository.findAll(pageable)
                .map(MemberListResponse::new);
    }

    // 회원의 게시글 수 조회
    public long countBoardsByMemberId(Long memberId) {
        return boardRepository.countByMemberId(memberId);
    }

    // 회원의 댓글 수 조회
    public long countCommentsByMemberId(Long memberId) {
        return commentRepository.countByMemberId(memberId);
    }

    // 역할 변경
    @Transactional
    public void changeRole(Long id, Role role) {
        Member member = findById(id);
        member.changeRole(role);
    }

    // 계정 활성화/비활성화 토글
    @Transactional
    public boolean toggleEnabled(Long id) {
        Member member = findById(id);
        member.setEnabled(!member.isEnabled());
        return member.isEnabled();
    }

    // 회원 삭제 (관리자용)
    @Transactional
    public void deleteMember(Long id) {
        Member member = findById(id);

        // 관리자는 삭제할 수 없음
        if (member.getRole() == Role.ADMIN) {
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다.");
        }

        memberRepository.delete(member);
    }

    // 프로필 수정 - 13장/02
    @Transactional
    public void updateProfile(Long memberId, ProfileEditRequest request) {
        Member member = findById(memberId);

        // 이메일 중복 체크 (본인 제외)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            memberRepository.findByEmail(request.getEmail())
                    .filter(m -> !m.getId().equals(memberId))
                    .ifPresent(m -> {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
                    });
        }

        member.updateInfo(request.getName(), request.getEmail());
    }

    // 비밀번호 변경 - 13장/03
    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest request) {
        Member member = findById(memberId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다");
        }

        // 새 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다");
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    // 회원 탈퇴 - 추가
    @Transactional
    public void deleteMember(Long memberId, MemberDeleteRequest request) {
        Member member = findById(memberId);

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다");
        }

        // 작성한 게시글과 댓글의 작성자를 null로 변경 (익명 처리)
        boardRepository.findByMemberId(memberId)
                .forEach(board -> board.updateMember(null));

        commentRepository.findByMemberId(memberId)
                .forEach(comment -> comment.updateMember(null));

        // 회원 삭제
        memberRepository.delete(member);
    }
}
