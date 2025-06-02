package com.example.its.domain.issue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {
    
    @Mock
    private IssueRepository issueRepository;
    
    @InjectMocks
    private IssueService issueService;
    
    @Nested
    @DisplayName("課題検索機能テスト")
    class FindIssuesTest {
        
        @Test
        @DisplayName("TODO課題一覧を正しく取得できる")
        void findAllTodoIssues_ShouldReturnTodoIssues() {
            // Given
            List<IssueEntity> mockTodoIssues = Arrays.asList(
                new IssueEntity(1L, "課題1", "説明1", IssueStatus.TODO, null),
                new IssueEntity(2L, "課題2", "説明2", IssueStatus.TODO, 1L)
            );
            when(issueRepository.findAllTodoIssues()).thenReturn(mockTodoIssues);
            
            // When
            List<IssueEntity> result = issueService.findAllTodoIssues();
            
            // Then
            assertThat(result)
                .hasSize(2)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.TODO);
            verify(issueRepository).findAllTodoIssues();
        }
        
        @Test
        @DisplayName("DOING課題一覧を正しく取得できる")
        void findAllDoingIssues_ShouldReturnDoingIssues() {
            // Given
            List<IssueEntity> mockDoingIssues = Arrays.asList(
                new IssueEntity(3L, "作業中課題", "作業中", IssueStatus.DOING, 2L)
            );
            when(issueRepository.findAllDoingIssues()).thenReturn(mockDoingIssues);
            
            // When
            List<IssueEntity> result = issueService.findAllDoingIssues();
            
            // Then
            assertThat(result)
                .hasSize(1)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.DOING);
            verify(issueRepository).findAllDoingIssues();
        }
        
        @Test
        @DisplayName("DONE課題一覧を正しく取得できる")
        void findAllDoneIssues_ShouldReturnDoneIssues() {
            // Given
            List<IssueEntity> mockDoneIssues = Arrays.asList(
                new IssueEntity(4L, "完了課題", "完了済み", IssueStatus.DONE, 1L)
            );
            when(issueRepository.findAllDoneIssues()).thenReturn(mockDoneIssues);
            
            // When
            List<IssueEntity> result = issueService.findAllDoneIssues();
            
            // Then
            assertThat(result)
                .hasSize(1)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.DONE);
            verify(issueRepository).findAllDoneIssues();
        }
        
        @Test
        @DisplayName("空の課題リストが正常に処理される")
        void findAllTodoIssues_WithEmptyList_ShouldReturnEmptyList() {
            // Given
            when(issueRepository.findAllTodoIssues()).thenReturn(Collections.emptyList());
            
            // When
            List<IssueEntity> result = issueService.findAllTodoIssues();
            
            // Then
            assertThat(result).isEmpty();
            verify(issueRepository).findAllTodoIssues();
        }
        
        @Test
        @DisplayName("存在する課題IDで課題を正しく取得できる")
        void findById_WithExistingId_ShouldReturnIssue() {
            // Given
            Long issueId = 1L;
            IssueEntity mockIssue = new IssueEntity(issueId, "テスト課題", "説明", IssueStatus.TODO, null);
            when(issueRepository.findById(issueId)).thenReturn(mockIssue);
            
            // When
            IssueEntity result = issueService.findById(issueId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(issueId);
            assertThat(result.getSummary()).isEqualTo("テスト課題");
            verify(issueRepository).findById(issueId);
        }
    }
    
    @Nested
    @DisplayName("課題作成テスト")
    class CreateIssueTest {
        
        @Test
        @DisplayName("基本的な課題作成が正常に動作する")
        void insert_WithValidData_ShouldCallRepository() {
            // Given
            String summary = "テスト課題";
            String description = "テスト説明";
            
            // When
            issueService.insert(summary, description);
            
            // Then
            verify(issueRepository).insert(summary, description);
        }
        
        @Test
        @DisplayName("担当者付き課題作成が正常に動作する")
        void insertWithAssignee_WithValidData_ShouldCallRepository() {
            // Given
            String summary = "担当者付き課題";
            String description = "説明";
            Long assigneeId = 1L;
            
            // When
            issueService.insertWithAssignee(summary, description, assigneeId);
            
            // Then
            verify(issueRepository).insertWithAssignee(summary, description, assigneeId);
        }
        
        @Test
        @DisplayName("空文字の課題概要でも処理される")
        void insert_WithEmptyString_ShouldStillCallRepository() {
            // Given
            String summary = "";
            String description = "説明";
            
            // When
            issueService.insert(summary, description);
            
            // Then
            verify(issueRepository).insert(summary, description);
        }
    }
    
    @Nested
    @DisplayName("ステータス変更テスト")
    class StatusChangeTest {
        
        @Test
        @DisplayName("次ステータス移行が正常に動作する")
        void moveToNextStatus_ShouldCallRepository() {
            // Given
            Long issueId = 1L;
            
            // When
            issueService.moveToNextStatus(issueId);
            
            // Then
            verify(issueRepository).moveToNextStatus(issueId);
        }
        
        @Test
        @DisplayName("前ステータス移行が正常に動作する")
        void moveToPreviousStatus_ShouldCallRepository() {
            // Given
            Long issueId = 1L;
            
            // When
            issueService.moveToPreviousStatus(issueId);
            
            // Then
            verify(issueRepository).moveToPreviousStatus(issueId);
        }
    }
    
    @Nested
    @DisplayName("複雑なビジネスロジックテスト")
    class ComplexBusinessLogicTest {
        
        @Test
        @DisplayName("完了課題のトグル機能 - DONE から TODO への変更")
        void completeIssue_WhenDone_ShouldMoveToTodo() {
            // Given
            Long issueId = 1L;
            IssueEntity doneIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.DONE, null);
            when(issueRepository.findById(issueId)).thenReturn(doneIssue);
            
            // When
            issueService.completeIssue(issueId);
            
            // Then
            verify(issueRepository, times(2)).moveToPreviousStatus(issueId);
        }
        
        @Test
        @DisplayName("完了課題のトグル機能 - TODO から DONE への変更")
        void completeIssue_WhenTodo_ShouldMoveToDone() {
            // Given
            Long issueId = 1L;
            IssueEntity todoIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.TODO, null);
            IssueEntity doingIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.DOING, null);
            IssueEntity doneIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.DONE, null);
            
            when(issueRepository.findById(issueId))
                .thenReturn(todoIssue)  // 最初の呼び出し
                .thenReturn(doingIssue) // while文の最初のチェック
                .thenReturn(doneIssue); // while文の二回目のチェック
            
            // When
            issueService.completeIssue(issueId);
            
            // Then
            verify(issueRepository, times(2)).moveToNextStatus(issueId);
        }
        
        @Test
        @DisplayName("完了課題のトグル機能 - DOING から DONE への変更")
        void completeIssue_WhenDoing_ShouldMoveToDone() {
            // Given
            Long issueId = 1L;
            IssueEntity doingIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.DOING, null);
            IssueEntity doneIssue = new IssueEntity(issueId, "課題", "説明", IssueStatus.DONE, null);
            
            when(issueRepository.findById(issueId))
                .thenReturn(doingIssue)  // 最初の呼び出し
                .thenReturn(doneIssue);  // while文のチェック
            
            // When
            issueService.completeIssue(issueId);
            
            // Then
            verify(issueRepository, times(1)).moveToNextStatus(issueId);
        }
    }
    
    @Nested
    @DisplayName("課題操作テスト")
    class IssueOperationsTest {
        
        @Test
        @DisplayName("課題削除が正常に動作する")
        void delete_ShouldCallRepository() {
            // Given
            Long issueId = 1L;
            
            // When
            issueService.delete(issueId);
            
            // Then
            verify(issueRepository).delete(issueId);
        }
        
        @Test
        @DisplayName("担当者更新が正常に動作する")
        void updateAssignee_ShouldCallRepository() {
            // Given
            Long issueId = 1L;
            Long assigneeId = 2L;
            
            // When
            issueService.updateAssignee(issueId, assigneeId);
            
            // Then
            verify(issueRepository).updateAssignee(issueId, assigneeId);
        }
        
        @Test
        @DisplayName("後方互換性 - 未完了課題取得が正常に動作する")
        void findAllIncompleteIssues_ShouldCallRepository() {
            // Given
            List<IssueEntity> mockIncompleteIssues = Arrays.asList(
                new IssueEntity(1L, "TODO課題", "説明", IssueStatus.TODO, null),
                new IssueEntity(2L, "DOING課題", "説明", IssueStatus.DOING, 1L)
            );
            when(issueRepository.findAllIncompleteIssues()).thenReturn(mockIncompleteIssues);
            
            // When
            List<IssueEntity> result = issueService.findAllIncompleteIssues();
            
            // Then
            assertThat(result).hasSize(2);
            verify(issueRepository).findAllIncompleteIssues();
        }
        
        @Test
        @DisplayName("後方互換性 - 完了課題取得が正常に動作する")
        void findAllCompletedIssues_ShouldCallRepository() {
            // Given
            List<IssueEntity> mockCompletedIssues = Arrays.asList(
                new IssueEntity(3L, "完了課題", "説明", IssueStatus.DONE, 1L)
            );
            when(issueRepository.findAllCompletedIssues()).thenReturn(mockCompletedIssues);
            
            // When
            List<IssueEntity> result = issueService.findAllCompletedIssues();
            
            // Then
            assertThat(result).hasSize(1);
            verify(issueRepository).findAllCompletedIssues();
        }
    }
} 