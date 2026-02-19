package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(String name, String email, String password) {
        // 이메일 중복 체크
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

    // 회원 조회
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
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
        member.updateName(name);  // 변경 감지로 자동 UPDATE
    }

    // 회원 삭제
    @Transactional
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}
