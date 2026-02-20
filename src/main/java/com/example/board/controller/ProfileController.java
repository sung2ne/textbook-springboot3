// 수정: src/main/java/com/example/board/controller/ProfileController.java
package com.example.board.controller;

import com.example.board.domain.Member;
import com.example.board.dto.MemberDeleteRequest;
import com.example.board.dto.PasswordChangeRequest;
import com.example.board.dto.ProfileEditRequest;
import com.example.board.security.CustomUserDetails;
import com.example.board.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final MemberService memberService;

    // 프로필 조회 - 13장/01
    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails,
                          Model model) {
        Member member = memberService.findById(userDetails.getMemberId());
        model.addAttribute("member", member);
        model.addAttribute("boardCount", memberService.countBoardsByMemberId(member.getId()));
        model.addAttribute("commentCount", memberService.countCommentsByMemberId(member.getId()));
        return "members/profile";
    }

    // 프로필 수정 폼 - 13장/02
    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        Member member = memberService.findById(userDetails.getMemberId());
        ProfileEditRequest request = new ProfileEditRequest();
        request.setName(member.getName());
        request.setEmail(member.getEmail());

        model.addAttribute("profileEditRequest", request);
        return "members/profile-edit";
    }

    // 프로필 수정 처리 - 13장/02
    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetails userDetails,
                       @Valid @ModelAttribute ProfileEditRequest request,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/profile-edit";
        }

        try {
            memberService.updateProfile(userDetails.getMemberId(), request);
            redirectAttributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
            return "redirect:/members/profile";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("updateFailed", e.getMessage());
            return "members/profile-edit";
        }
    }

    // 비밀번호 변경 폼 - 13장/03
    @GetMapping("/password")
    public String passwordForm(Model model) {
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        return "members/password-change";
    }

    // 비밀번호 변경 처리 - 13장/03
    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @Valid @ModelAttribute PasswordChangeRequest request,
                                 BindingResult bindingResult,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/password-change";
        }

        try {
            memberService.changePassword(userDetails.getMemberId(), request);

            // 비밀번호 변경 후 로그아웃 처리
            new SecurityContextLogoutHandler().logout(
                    httpRequest, httpResponse,
                    SecurityContextHolder.getContext().getAuthentication()
            );

            redirectAttributes.addFlashAttribute("message",
                    "비밀번호가 변경되었습니다. 새 비밀번호로 다시 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("passwordChangeFailed", e.getMessage());
            return "members/password-change";
        }
    }

    // 회원 탈퇴 폼 - 13장/04
    @GetMapping("/delete")
    public String deleteForm(Model model) {
        model.addAttribute("memberDeleteRequest", new MemberDeleteRequest());
        return "members/member-delete";
    }

    // 회원 탈퇴 처리 - 13장/04
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute MemberDeleteRequest request,
                         BindingResult bindingResult,
                         HttpServletRequest httpRequest,
                         HttpServletResponse httpResponse) {
        if (bindingResult.hasErrors()) {
            return "members/member-delete";
        }

        try {
            memberService.deleteMember(userDetails.getMemberId(), request);

            // 탈퇴 후 로그아웃 처리 (세션 무효화)
            new SecurityContextLogoutHandler().logout(
                    httpRequest, httpResponse,
                    SecurityContextHolder.getContext().getAuthentication()
            );

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("deleteFailed", e.getMessage());
            return "members/member-delete";
        }
    }
}
