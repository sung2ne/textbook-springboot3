// 수정: src/main/java/com/example/board/controller/AdminController.java
package com.example.board.controller;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import com.example.board.dto.MemberListResponse;
import com.example.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MemberService memberService;

    // 회원 목록 조회
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Role role,
                       @PageableDefault(size = 20, sort = "createdAt",
                               direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {

        Page<MemberListResponse> members = memberService.findAllMembers(keyword, role, pageable);

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("roles", Role.values());

        return "admin/members/list";
    }

    // 회원 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        model.addAttribute("boardCount", memberService.countBoardsByMemberId(id));
        model.addAttribute("commentCount", memberService.countCommentsByMemberId(id));
        return "admin/members/detail";
    }

    // 역할 변경 (AJAX)
    @PostMapping("/{id}/role")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changeRole(@PathVariable Long id,
                                                          @RequestParam Role role) {
        try {
            memberService.changeRole(id, role);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "역할이 변경되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 계정 활성화/비활성화 토글 (AJAX)
    @PostMapping("/{id}/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleEnabled(@PathVariable Long id) {
        try {
            boolean enabled = memberService.toggleEnabled(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "enabled", enabled,
                    "message", enabled ? "계정이 활성화되었습니다." : "계정이 비활성화되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 회원 삭제 (AJAX)
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원이 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
