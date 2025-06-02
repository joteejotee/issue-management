package com.example.its.web.issue;

import com.example.its.domain.assignee.AssigneeEntity;
import com.example.its.domain.assignee.AssigneeService;
import com.example.its.domain.issue.IssueEntity;
import com.example.its.domain.issue.IssueService;
import com.example.its.domain.issue.IssueStatus;
import com.example.its.domain.user.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
class IssueControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IssueService issueService;
    
    @MockBean
    private AssigneeService assigneeService;
    
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    
    @Nested
    @DisplayName("課題一覧表示テスト")
    class IssueListTest {
        
        @Test
        @WithMockUser
        @DisplayName("課題一覧が正常に表示される")
        void listIssues_ShouldDisplayAllIssues() throws Exception {
            // Given
            List<IssueEntity> todoIssues = Arrays.asList(
                new IssueEntity(1L, "TODO課題", "説明", IssueStatus.TODO, 1L)
            );
            List<IssueEntity> doingIssues = Arrays.asList(
                new IssueEntity(2L, "DOING課題", "説明", IssueStatus.DOING, 1L)
            );
            List<IssueEntity> doneIssues = Arrays.asList(
                new IssueEntity(3L, "DONE課題", "説明", IssueStatus.DONE, 1L)
            );
            List<AssigneeEntity> assignees = Arrays.asList(
                new AssigneeEntity(1L, "担当者", "/avatar.jpg")
            );
            
            when(issueService.findAllTodoIssues()).thenReturn(todoIssues);
            when(issueService.findAllDoingIssues()).thenReturn(doingIssues);
            when(issueService.findAllDoneIssues()).thenReturn(doneIssues);
            when(issueService.findAllIncompleteIssues()).thenReturn(Arrays.asList(todoIssues.get(0), doingIssues.get(0)));
            when(issueService.findAllCompletedIssues()).thenReturn(doneIssues);
            when(assigneeService.findAll()).thenReturn(assignees);
            
            // When & Then
            mockMvc.perform(get("/issues"))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/list"))
                .andExpect(model().attributeExists("todoIssues", "doingIssues", "doneIssues"))
                .andExpect(model().attribute("todoIssues", hasSize(1)))
                .andExpect(model().attribute("doingIssues", hasSize(1)))
                .andExpect(model().attribute("doneIssues", hasSize(1)))
                .andExpect(model().attribute("assignees", hasSize(1)));
        }
        
        @Test
        @DisplayName("認証なしで課題一覧にアクセスするとログインページにリダイレクトされる")
        void listIssues_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            // When & Then
            mockMvc.perform(get("/issues"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        }
    }
    
    @Nested
    @DisplayName("課題作成テスト")
    class IssueCreationTest {
        
        @Test
        @WithMockUser
        @DisplayName("課題作成フォームが正常に表示される")
        void showCreationForm_ShouldDisplayForm() throws Exception {
            // Given
            List<AssigneeEntity> assignees = Arrays.asList(
                new AssigneeEntity(1L, "担当者1", "/avatar1.jpg"),
                new AssigneeEntity(2L, "担当者2", "/avatar2.jpg")
            );
            when(assigneeService.findAll()).thenReturn(assignees);
            
            // When & Then
            mockMvc.perform(get("/issues/creationForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/creationForm"))
                .andExpect(model().attributeExists("assignees"))
                .andExpect(model().attribute("assignees", hasSize(2)));
        }
        
        @Test
        @WithMockUser
        @DisplayName("担当者なしで課題が正常に作成される")
        void createIssue_WithoutAssignee_ShouldSucceed() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues")
                    .param("summary", "新しい課題")
                    .param("description", "課題の説明")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/issues"));
            
            verify(issueService).insert("新しい課題", "課題の説明");
            verify(issueService, never()).insertWithAssignee(anyString(), anyString(), anyLong());
        }
        
        @Test
        @WithMockUser
        @DisplayName("担当者付きで課題が正常に作成される")
        void createIssue_WithAssignee_ShouldSucceed() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues")
                    .param("summary", "担当者付き課題")
                    .param("description", "課題の説明")
                    .param("assigneeId", "1")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/issues"));
            
            verify(issueService).insertWithAssignee("担当者付き課題", "課題の説明", 1L);
            verify(issueService, never()).insert(anyString(), anyString());
        }
    }
    
    @Nested
    @DisplayName("課題詳細表示テスト")
    class IssueDetailTest {
        
        @Test
        @WithMockUser
        @DisplayName("担当者なしの課題詳細が正常に表示される")
        void getIssueDetails_WithoutAssignee_ShouldDisplayDetails() throws Exception {
            // Given
            IssueEntity issue = new IssueEntity(1L, "課題", "説明", IssueStatus.TODO, null);
            when(issueService.findById(1L)).thenReturn(issue);
            
            // When & Then
            mockMvc.perform(get("/issues/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/detail"))
                .andExpect(model().attributeExists("issue"))
                .andExpect(model().attribute("issue", issue));
            
            verify(assigneeService, never()).findById(any());
        }
        
        @Test
        @WithMockUser
        @DisplayName("担当者付きの課題詳細が正常に表示される")
        void getIssueDetails_WithAssignee_ShouldDisplayDetailsWithAssignee() throws Exception {
            // Given
            IssueEntity issue = new IssueEntity(1L, "課題", "説明", IssueStatus.TODO, 1L);
            AssigneeEntity assignee = new AssigneeEntity(1L, "担当者", "/avatar.jpg");
            
            when(issueService.findById(1L)).thenReturn(issue);
            when(assigneeService.findById(1L)).thenReturn(java.util.Optional.of(assignee));
            
            // When & Then
            mockMvc.perform(get("/issues/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/detail"))
                .andExpect(model().attributeExists("issue", "assignee"))
                .andExpect(model().attribute("issue", issue))
                .andExpect(model().attribute("assignee", assignee));
        }
    }
    
    @Nested
    @DisplayName("ステータス変更テスト")
    class StatusChangeTest {
        
        @Test
        @WithMockUser
        @DisplayName("次ステータス移行が正常に動作する")
        void moveToNextStatus_ShouldSucceed() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues/next")
                    .param("id", "1")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/issues"));
            
            verify(issueService).moveToNextStatus(1L);
        }
        
        @Test
        @WithMockUser
        @DisplayName("前ステータス移行が正常に動作する")
        void moveToPreviousStatus_ShouldSucceed() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues/previous")
                    .param("id", "2")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/issues"));
            
            verify(issueService).moveToPreviousStatus(2L);
        }
        
        @Test
        @WithMockUser
        @DisplayName("完了トグル機能が正常に動作する")
        void completeIssue_ShouldSucceed() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues/complete")
                    .param("id", "3")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/issues"));
            
            verify(issueService).completeIssue(3L);
        }
        
        @Test
        @DisplayName("認証なしでステータス変更するとログインページにリダイレクトされる")
        void statusChange_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            // When & Then
            mockMvc.perform(post("/issues/next")
                    .param("id", "1")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
            
            verify(issueService, never()).moveToNextStatus(any());
        }
    }
} 