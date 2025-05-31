package com.example.its.domain.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    // TODO状態の課題を取得
    @Transactional
    public List<IssueEntity> findAllTodoIssues() {
        return issueRepository.findAllTodoIssues();
    }

    // DOING状態の課題を取得
    @Transactional
    public List<IssueEntity> findAllDoingIssues() {
        return issueRepository.findAllDoingIssues();
    }

    // DONE状態の課題を取得
    @Transactional
    public List<IssueEntity> findAllDoneIssues() {
        return issueRepository.findAllDoneIssues();
    }

    // 未完了の課題をリポジトリから全て取得して返す
    @Transactional
    public List<IssueEntity> findAllIncompleteIssues() {
        return issueRepository.findAllIncompleteIssues();
    }

    // 完了した課題をリポジトリから全て取得して返す
    @Transactional
    public List<IssueEntity> findAllCompletedIssues() {
        return issueRepository.findAllCompletedIssues();
    }

    // 課題のステータスを次の段階に進める
    @Transactional
    public void moveToNextStatus(Long id) {
        issueRepository.moveToNextStatus(id);
    }

    // 課題のステータスを前の段階に戻す
    @Transactional
    public void moveToPreviousStatus(Long id) {
        issueRepository.moveToPreviousStatus(id);
    }

    // 課題のステータスを完了状態に更新する
    @Transactional
    public void completeIssue(Long id) {
        // 既存の動作を維持：完了状態をトグル
        IssueEntity issue = issueRepository.findById(id);
        if (issue.getStatus() == IssueStatus.DONE) {
            // 完了 → TODO に戻す
            issueRepository.moveToPreviousStatus(id);
            issueRepository.moveToPreviousStatus(id); // DOING → TODO
        } else {
            // TODO/DOING → DONE に進める
            while (issueRepository.findById(id).getStatus() != IssueStatus.DONE) {
                issueRepository.moveToNextStatus(id);
            }
        }
    }

    // リポジトリから指定されたIDの課題を取得して返す(既存のメソッドをそのまま保持)
    @Transactional
    public IssueEntity findById(Long id) {
        return issueRepository.findById(id);
    }

    // 新しい課題をDBに挿入する
    @Transactional
    public void insert(String summary, String description) {
        issueRepository.insert(summary, description);
    }

    // 担当者付きで新しい課題をDBに挿入する
    @Transactional
    public void insertWithAssignee(String summary, String description, Long assigneeId) {
        issueRepository.insertWithAssignee(summary, description, assigneeId);
    }

    // 課題の担当者を更新する
    @Transactional
    public void updateAssignee(Long issueId, Long assigneeId) {
        issueRepository.updateAssignee(issueId, assigneeId);
    }

    // 指定されたIDの課題を削除する
    @Transactional
    public void delete(Long id) {
        issueRepository.delete(id);
    }
}
