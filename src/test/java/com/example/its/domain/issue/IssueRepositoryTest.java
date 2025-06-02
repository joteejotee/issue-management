package com.example.its.domain.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class IssueRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("its_test")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private IssueRepository issueRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Nested
    @DisplayName("課題ステータス別取得テスト")
    class FindByStatusTest {
        
        @BeforeEach
        void setUp() {
            // テストデータの準備
            issueRepository.insert("TODO課題1", "TODO課題の説明1");
            issueRepository.insert("TODO課題2", "TODO課題の説明2");
            
            issueRepository.insertWithAssignee("担当者付き課題", "説明", 1L);
            // 3番目の課題をDOING状態に変更
            issueRepository.moveToNextStatus(3L);
        }
        
        @Test
        @DisplayName("TODO状態の課題を正しく取得できる")
        void findAllTodoIssues_ShouldReturnTodoIssues() {
            // When
            List<IssueEntity> todoIssues = issueRepository.findAllTodoIssues();
            
            // Then
            assertThat(todoIssues).hasSize(2);
            assertThat(todoIssues)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.TODO);
        }
        
        @Test
        @DisplayName("DOING状態の課題を正しく取得できる")
        void findAllDoingIssues_ShouldReturnDoingIssues() {
            // When
            List<IssueEntity> doingIssues = issueRepository.findAllDoingIssues();
            
            // Then
            assertThat(doingIssues).hasSize(1);
            assertThat(doingIssues.get(0).getStatus()).isEqualTo(IssueStatus.DOING);
        }
        
        @Test
        @DisplayName("DONE状態の課題を正しく取得できる")
        void findAllDoneIssues_ShouldReturnDoneIssues() {
            // When
            List<IssueEntity> doneIssues = issueRepository.findAllDoneIssues();
            
            // Then
            assertThat(doneIssues).isEmpty();
        }
        
        @Test
        @DisplayName("未完了課題（TODO + DOING）を正しく取得できる")
        void findAllIncompleteIssues_ShouldReturnIncompleteIssues() {
            // When
            List<IssueEntity> incompleteIssues = issueRepository.findAllIncompleteIssues();
            
            // Then
            assertThat(incompleteIssues).hasSize(3);
            assertThat(incompleteIssues)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.TODO, IssueStatus.DOING);
        }
    }
    
    @Nested
    @DisplayName("ステータス変更テスト")
    class StatusChangeTest {
        
        private Long testIssueId;
        
        @BeforeEach
        void setUp() {
            issueRepository.insert("テスト課題", "説明");
            testIssueId = 1L;
        }
        
        @Test
        @DisplayName("TODO → DOING → DONE → TODO の順でステータスが変更される")
        void moveToNextStatus_ShouldTransitionCorrectly() {
            // 初期状態: TODO
            IssueEntity issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.TODO);
            
            // TODO → DOING
            issueRepository.moveToNextStatus(testIssueId);
            issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.DOING);
            
            // DOING → DONE
            issueRepository.moveToNextStatus(testIssueId);
            issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.DONE);
            
            // DONE → TODO
            issueRepository.moveToNextStatus(testIssueId);
            issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.TODO);
        }
        
        @Test
        @DisplayName("DONE → DOING → TODO の順でステータスが戻る")
        void moveToPreviousStatus_ShouldTransitionCorrectly() {
            // DONE状態に設定
            issueRepository.moveToNextStatus(testIssueId); // TODO → DOING
            issueRepository.moveToNextStatus(testIssueId); // DOING → DONE
            
            IssueEntity issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.DONE);
            
            // DONE → DOING
            issueRepository.moveToPreviousStatus(testIssueId);
            issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.DOING);
            
            // DOING → TODO
            issueRepository.moveToPreviousStatus(testIssueId);
            issue = issueRepository.findById(testIssueId);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.TODO);
        }
    }
    
    @Nested
    @DisplayName("課題CRUD操作テスト")
    class CrudOperationsTest {
        
        @Test
        @DisplayName("課題の作成が正常に動作する")
        void insert_ShouldCreateIssue() {
            // When
            issueRepository.insert("新しい課題", "課題の説明");
            
            // Then
            List<IssueEntity> allIssues = issueRepository.findAllTodoIssues();
            assertThat(allIssues).hasSize(1);
            
            IssueEntity createdIssue = allIssues.get(0);
            assertThat(createdIssue.getSummary()).isEqualTo("新しい課題");
            assertThat(createdIssue.getDescription()).isEqualTo("課題の説明");
            assertThat(createdIssue.getStatus()).isEqualTo(IssueStatus.TODO);
        }
        
        @Test
        @DisplayName("担当者付き課題の作成が正常に動作する")
        void insertWithAssignee_ShouldCreateIssueWithAssignee() {
            // When
            issueRepository.insertWithAssignee("担当者付き課題", "説明", 1L);
            
            // Then
            List<IssueEntity> allIssues = issueRepository.findAllTodoIssues();
            assertThat(allIssues).hasSize(1);
            
            IssueEntity createdIssue = allIssues.get(0);
            assertThat(createdIssue.getAssigneeId()).isEqualTo(1L);
        }
        
        @Test
        @DisplayName("課題の削除が正常に動作する")
        void delete_ShouldRemoveIssue() {
            // Given
            issueRepository.insert("削除テスト課題", "説明");
            Long issueId = 1L;
            
            // When
            issueRepository.delete(issueId);
            
            // Then
            IssueEntity deletedIssue = issueRepository.findById(issueId);
            assertThat(deletedIssue).isNull();
        }
        
        @Test
        @DisplayName("担当者の更新が正常に動作する")
        void updateAssignee_ShouldUpdateAssignee() {
            // Given
            issueRepository.insert("課題", "説明");
            Long issueId = 1L;
            Long newAssigneeId = 2L;
            
            // When
            issueRepository.updateAssignee(issueId, newAssigneeId);
            
            // Then
            IssueEntity updatedIssue = issueRepository.findById(issueId);
            assertThat(updatedIssue.getAssigneeId()).isEqualTo(newAssigneeId);
        }
    }
} 