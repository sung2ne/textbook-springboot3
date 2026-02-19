// 새 파일: src/test/java/com/example/board/controller/BoardControllerTest.java
package com.example.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게시판 목록 조회 - 비로그인")
    void list_anonymous() throws Exception {
        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"));
    }

    @Test
    @DisplayName("게시글 작성 폼 - 비로그인 시 로그인 페이지로 이동")
    void createForm_anonymous() throws Exception {
        mockMvc.perform(get("/boards/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("게시글 작성 폼 - 로그인 상태")
    @WithMockUser(username = "testuser", roles = "USER")
    void createForm_authenticated() throws Exception {
        mockMvc.perform(get("/boards/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"));
    }
}
