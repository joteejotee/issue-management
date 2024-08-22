package com.example.its.domain.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

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

    // 指定されたIDの課題を完了状態に更新する
    @Transactional
    public void completeIssue(Long id) {
        issueRepository.completeIssue(id);
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

    // 指定されたIDの課題を削除する
    @Transactional
    public void delete(Long id) {
        issueRepository.delete(id);
    }
}
