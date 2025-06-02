package com.example.its.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Nested
    @DisplayName("認証なしでアクセス可能なエンドポイント")
    class PublicEndpointTest {
        
        @Test
        @DisplayName("ログインページは認証なしでアクセス可能")
        void loginPage_ShouldBeAccessibleWithoutAuth() throws Exception {
            mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
        }
        
        @Test
        @DisplayName("ホームページは認証なしでアクセス可能")
        void homePage_ShouldBeAccessibleWithoutAuth() throws Exception {
            mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        }
    }
    
    @Nested
    @DisplayName("認証が必要なエンドポイント")
    class ProtectedEndpointTest {
        
        @Test
        @DisplayName("課題一覧は認証なしでアクセスするとリダイレクトされる")
        void issuesPage_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            mockMvc.perform(get("/issues"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        }
        
        @Test
        @DisplayName("担当者一覧は認証なしでアクセスするとリダイレクトされる")
        void assigneesPage_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            mockMvc.perform(get("/assignees"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        }
    }
    
    @Nested
    @DisplayName("ユーザー権限での機能テスト")
    class UserRoleTest {
        
        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("USER権限で課題一覧にアクセス可能")
        void userRole_ShouldAccessIssuesList() throws Exception {
            mockMvc.perform(get("/issues"))
                .andExpect(status().isOk());
        }
        
        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("USER権限で担当者一覧にアクセス可能")
        void userRole_ShouldAccessAssigneesList() throws Exception {
            mockMvc.perform(get("/assignees"))
                .andExpect(status().isOk());
        }
        
        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("USER権限で課題作成可能")
        void userRole_ShouldCreateIssue() throws Exception {
            mockMvc.perform(post("/issues")
                    .param("summary", "テスト")
                    .param("description", "説明")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection());
        }
    }
    
    @Nested
    @DisplayName("CSRF保護テスト")
    class CsrfProtectionTest {
        
        @Test
        @WithMockUser
        @DisplayName("CSRFトークンなしでPOSTするとForbiddenになる")
        void postWithoutCsrf_ShouldBeForbidden() throws Exception {
            mockMvc.perform(post("/issues")
                    .param("summary", "テスト")
                    .param("description", "説明"))
                .andExpect(status().isForbidden());
        }
        
        @Test
        @WithMockUser
        @DisplayName("CSRFトークンありでPOSTすると成功する")
        void postWithCsrf_ShouldSucceed() throws Exception {
            mockMvc.perform(post("/issues")
                    .param("summary", "テスト")
                    .param("description", "説明")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection());
        }
    }
} 