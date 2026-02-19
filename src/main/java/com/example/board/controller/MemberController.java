// 새 파일: src/main/java/com/example/board/controller/MemberController.java
package com.example.board.controller;

import com.example.board.dto.SignupRequest;
import com.example.board.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "members/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/signup";
        }

        if (!signupRequest.isPasswordMatching()) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm",
                    "비밀번호가 일치하지 않습니다");
            return "members/signup";
        }

        try {
            memberService.signup(signupRequest);
            return "redirect:/login?signup";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "members/signup";
        }
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return !memberService.isUsernameDuplicate(username);
    }

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return !memberService.isEmailDuplicate(email);
    }
}
