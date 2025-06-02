package com.example.its.integration;

import com.example.its.domain.assignee.AssigneeEntity;
import com.example.its.domain.assignee.AssigneeRepository;
import com.example.its.domain.issue.IssueEntity;
import com.example.its.domain.issue.IssueRepository;
import com.example.its.domain.issue.IssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class IssueManagementIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private IssueRepository issueRepository;
    
    @Autowired
    private AssigneeRepository assigneeRepository;
    
    @BeforeEach
    void setUp() {
        issueRepository.deleteAll();
        assigneeRepository.deleteAll();
    }
    
    @Nested
    @DisplayName("課題のライフサイクルテスト")
    class IssueLifecycleTest {
        
        @Test
        @DisplayName("課題の完全なライフサイクル管理")
        void testCompleteIssueLifecycle() {
            // Given
            var assignee = new AssigneeEntity(null, "Test User", "/images/avatars/default.svg");
            assigneeRepository.insert(assignee);
            
            var issue = new IssueEntity(null, "Test Issue", "Test Description", IssueStatus.TODO, assignee);
            var savedIssue = issueRepository.save(issue);
            
            // When & Then - TODO → DOING
            issueRepository.moveToNextStatus(savedIssue.getId());
            var doingIssue = issueRepository.findById(savedIssue.getId());
            assertThat(doingIssue.getStatus()).isEqualTo(IssueStatus.DOING);
            
            // When & Then - DOING → DONE
            issueRepository.moveToNextStatus(savedIssue.getId());
            var doneIssue = issueRepository.findById(savedIssue.getId());
            assertThat(doneIssue.getStatus()).isEqualTo(IssueStatus.DONE);
            
            // When & Then - DONE → TODO (循環)
            issueRepository.moveToPreviousStatus(savedIssue.getId());
            var backToDoingIssue = issueRepository.findById(savedIssue.getId());
            assertThat(backToDoingIssue.getStatus()).isEqualTo(IssueStatus.DOING);
        }
        
        @Test
        @DisplayName("複数の課題を異なるステータスで管理")
        void testMultipleIssuesWithDifferentStatuses() {
            // Given - 複数課題作成
            var assignee = new AssigneeEntity(null, "Developer", "/images/avatars/default.svg");
            assigneeRepository.insert(assignee);
            
            // When - 各課題を異なるステータスに設定
            var issue1 = issueRepository.save(new IssueEntity(null, "Issue 1", "Description 1", IssueStatus.TODO, assignee));
            var issue2 = issueRepository.save(new IssueEntity(null, "Issue 2", "Description 2", IssueStatus.TODO, assignee));
            var issue3 = issueRepository.save(new IssueEntity(null, "Issue 3", "Description 3", IssueStatus.TODO, assignee));
            
            issueRepository.moveToNextStatus(issue1.getId());
            issueRepository.moveToNextStatus(issue2.getId());
            issueRepository.moveToNextStatus(issue2.getId());
            
            // Then - 各ステータスの課題数を確認
            var todoIssues = issueRepository.findAllTodoIssues();
            var doingIssues = issueRepository.findAllDoingIssues();
            var doneIssues = issueRepository.findAllDoneIssues();
            
            assertThat(todoIssues).hasSize(1);
            assertThat(doingIssues).hasSize(1);
            assertThat(doneIssues).hasSize(1);
        }
    }
    
    @Nested
    @DisplayName("担当者管理統合テスト")
    class AssigneeManagementTest {
        
        @Test
        @DisplayName("担当者の作成・更新・削除が正常に動作する")
        @Rollback
        void assigneeManagement_ShouldWorkCorrectly() {
            // 1. 担当者作成
            AssigneeEntity assignee = new AssigneeEntity(0, "統合テスト太郎", "/test/avatar.jpg");
            assigneeRepository.insert(assignee);
            
            // 作成確認
            List<AssigneeEntity> allAssignees = assigneeRepository.findAll();
            assertThat(allAssignees).hasSize(1);
            assertThat(assignee.getId()).isGreaterThan(0); // IDが自動生成される
            
            // 2. 担当者更新
            Long assigneeId = assignee.getId();
            AssigneeEntity updatedAssignee = new AssigneeEntity(assigneeId, "更新太郎", "/updated/avatar.jpg");
            assigneeRepository.update(updatedAssignee);
            
            // 更新確認
            var foundAssignee = assigneeRepository.findById(assigneeId);
            assertThat(foundAssignee).isPresent();
            assertThat(foundAssignee.get().getName()).isEqualTo("更新太郎");
            assertThat(foundAssignee.get().getPhotoUrl()).isEqualTo("/updated/avatar.jpg");
            
            // 3. 担当者削除
            assigneeRepository.delete(assigneeId);
            
            // 削除確認
            var deletedAssignee = assigneeRepository.findById(assigneeId);
            assertThat(deletedAssignee).isEmpty();
            
            List<AssigneeEntity> afterDelete = assigneeRepository.findAll();
            assertThat(afterDelete).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("課題と担当者の関連テスト")
    class IssueAssigneeRelationTest {
        
        @Test
        @DisplayName("担当者付き課題の作成・管理が正常に動作する")
        @Rollback
        void issueWithAssignee_ShouldWorkCorrectly() {
            // 1. 担当者作成
            AssigneeEntity assignee = new AssigneeEntity(0, "担当者太郎", "/avatar.jpg");
            assigneeRepository.insert(assignee);
            Long assigneeId = assignee.getId();
            
            // 2. 担当者付き課題作成
            issueRepository.insertWithAssignee("担当者付き課題", "説明", assigneeId);
            
            // 作成確認
            List<IssueEntity> issues = issueRepository.findAllTodoIssues();
            assertThat(issues).hasSize(1);
            assertThat(issues.get(0).getAssigneeId()).isEqualTo(assigneeId);
            
            // 3. 担当者変更
            AssigneeEntity newAssignee = new AssigneeEntity(0, "新担当者", "/new_avatar.jpg");
            assigneeRepository.insert(newAssignee);
            Long newAssigneeId = newAssignee.getId();
            
            IssueEntity issue = issues.get(0);
            issueRepository.updateAssignee(issue.getId(), newAssigneeId);
            
            // 担当者変更確認
            IssueEntity updatedIssue = issueRepository.findById(issue.getId());
            assertThat(updatedIssue.getAssigneeId()).isEqualTo(newAssigneeId);
            
            // 4. 担当者をnullに設定
            issueRepository.updateAssignee(issue.getId(), null);
            
            IssueEntity unassignedIssue = issueRepository.findById(issue.getId());
            assertThat(unassignedIssue.getAssigneeId()).isNull();
        }
        
        @Test
        @DisplayName("担当者削除時に課題の担当者がnullになる")
        @Rollback
        void deleteAssignee_ShouldSetIssueAssigneeToNull() {
            // 1. 担当者と課題作成
            AssigneeEntity assignee = new AssigneeEntity(0, "削除予定者", "/avatar.jpg");
            assigneeRepository.insert(assignee);
            Long assigneeId = assignee.getId();
            
            issueRepository.insertWithAssignee("課題1", "説明1", assigneeId);
            issueRepository.insertWithAssignee("課題2", "説明2", assigneeId);
            
            // 2. 担当者削除（ON DELETE SET NULLの動作確認）
            assigneeRepository.delete(assigneeId);
            
            // 3. 課題の担当者がnullになっていることを確認
            List<IssueEntity> issues = issueRepository.findAllTodoIssues();
            assertThat(issues).hasSize(2);
            assertThat(issues).allMatch(issue -> issue.getAssigneeId() == null);
        }
    }
    
    @Nested
    @DisplayName("データベーストランザクションテスト")
    class TransactionTest {
        
        @Test
        @DisplayName("複数操作が一貫性を保って実行される")
        @Rollback
        void multipleOperations_ShouldMaintainConsistency() {
            // 1. 初期データ作成
            issueRepository.insert("トランザクションテスト1", "説明1");
            issueRepository.insert("トランザクションテスト2", "説明2");
            issueRepository.insert("トランザクションテスト3", "説明3");
            
            List<IssueEntity> initialIssues = issueRepository.findAllTodoIssues();
            Long issue1Id = initialIssues.get(0).getId();
            Long issue2Id = initialIssues.get(1).getId();
            
            // 2. 複数のステータス変更
            issueRepository.moveToNextStatus(issue1Id); // TODO → DOING
            issueRepository.moveToNextStatus(issue1Id); // DOING → DONE
            issueRepository.moveToNextStatus(issue2Id); // TODO → DOING
            
            // 3. 一貫性確認
            List<IssueEntity> todoIssues = issueRepository.findAllTodoIssues();
            List<IssueEntity> doingIssues = issueRepository.findAllDoingIssues();
            List<IssueEntity> doneIssues = issueRepository.findAllDoneIssues();
            
            assertThat(todoIssues).hasSize(1);
            assertThat(doingIssues).hasSize(1);
            assertThat(doneIssues).hasSize(1);
            
            // 4. 未完了・完了課題の分類確認
            List<IssueEntity> incompleteIssues = issueRepository.findAllIncompleteIssues();
            List<IssueEntity> completedIssues = issueRepository.findAllCompletedIssues();
            
            assertThat(incompleteIssues).hasSize(2); // TODO + DOING
            assertThat(completedIssues).hasSize(1); // DONE
        }
    }
    
    @Nested
    @DisplayName("認証が必要なエンドポイントのテスト")
    class AuthenticationRequiredEndpointTest {
        
        @Test
        @DisplayName("認証なしでAPIにアクセスするとリダイレクトされる")
        void accessProtectedEndpoint_WithoutAuth_ShouldRedirect() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity("/issues", String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation().toString()).contains("login");
        }
        
        @Test
        @DisplayName("認証なしでホームページにアクセスできる")
        void accessHomePage_WithoutAuth_ShouldSucceed() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        
        @Test
        @DisplayName("静的リソースに認証なしでアクセスできる")
        void accessStaticResources_WithoutAuth_ShouldSucceed() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity("/css/style.css", String.class);
            
            // Then
            // ファイルが存在する場合は200、存在しない場合は404
            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
        }
    }
    
    @Nested
    @DisplayName("エンドツーエンドシナリオテスト")
    class EndToEndScenarioTest {
        
        @Test
        @DisplayName("新規プロジェクト開始からリリースまでのシナリオ")
        @Rollback
        void newProjectScenario_ShouldWorkEndToEnd() {
            // シナリオ: 新規プロジェクトで課題を管理
            
            // 1. チームメンバー登録
            AssigneeEntity developer = new AssigneeEntity(0, "開発太郎", "/avatars/dev.jpg");
            AssigneeEntity designer = new AssigneeEntity(0, "デザイン花子", "/avatars/design.jpg");
            assigneeRepository.insert(developer);
            assigneeRepository.insert(designer);
            
            Long devId = developer.getId();
            Long designId = designer.getId();
            
            // 2. プロジェクト課題作成
            issueRepository.insertWithAssignee("ユーザー認証機能", "ログイン機能の実装", devId);
            issueRepository.insertWithAssignee("UI/UXデザイン", "ワイヤーフレーム作成", designId);
            issueRepository.insert("データベース設計", "テーブル設計と正規化");
            
            // 3. 開発開始（TODO → DOING）
            List<IssueEntity> initialTodos = issueRepository.findAllTodoIssues();
            assertThat(initialTodos).hasSize(3);
            
            // デザイン作業開始（UI/UXデザインを見つけてDOINGに）
            IssueEntity designIssue = initialTodos.stream()
                .filter(issue -> issue.getSummary().equals("UI/UXデザイン"))
                .findFirst().orElseThrow();
            issueRepository.moveToNextStatus(designIssue.getId());
            
            // 4. デザイン完了
            issueRepository.moveToNextStatus(designIssue.getId());
            
            // 5. 開発作業開始
            IssueEntity authIssue = initialTodos.stream()
                .filter(issue -> issue.getSummary().equals("ユーザー認証機能"))
                .findFirst().orElseThrow();
            IssueEntity dbIssue = initialTodos.stream()
                .filter(issue -> issue.getSummary().equals("データベース設計"))
                .findFirst().orElseThrow();
            
            issueRepository.moveToNextStatus(authIssue.getId());
            issueRepository.moveToNextStatus(dbIssue.getId());
            
            // 6. プロジェクト状況確認
            List<IssueEntity> currentTodos = issueRepository.findAllTodoIssues();
            List<IssueEntity> currentDoings = issueRepository.findAllDoingIssues();
            List<IssueEntity> currentDones = issueRepository.findAllDoneIssues();
            
            assertThat(currentTodos).isEmpty();
            assertThat(currentDoings).hasSize(2);
            assertThat(currentDones).hasSize(1);
            
            // 7. 全タスク完了
            issueRepository.moveToNextStatus(authIssue.getId());
            issueRepository.moveToNextStatus(dbIssue.getId());
            
            // 8. プロジェクト完了確認
            List<IssueEntity> finalCompleted = issueRepository.findAllCompletedIssues();
            List<IssueEntity> finalIncomplete = issueRepository.findAllIncompleteIssues();
            
            assertThat(finalCompleted).hasSize(3);
            assertThat(finalIncomplete).isEmpty();
            assertThat(finalCompleted)
                .extracting(IssueEntity::getStatus)
                .containsOnly(IssueStatus.DONE);
        }
    }
} 