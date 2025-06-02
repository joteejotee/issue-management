package com.example.its.domain.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public List<Issue> findTodoIssues() {
        return issueRepository.findTodoIssues();
    }

    public List<Issue> findDoingIssues() {
        return issueRepository.findDoingIssues();
    }

    public List<Issue> findDoneIssues() {
        return issueRepository.findDoneIssues();
    }

    public List<Issue> findIncompleteIssues() {
        var todoIssues = issueRepository.findTodoIssues();
        var doingIssues = issueRepository.findDoingIssues();
        return Stream.concat(todoIssues.stream(), doingIssues.stream())
                .collect(Collectors.toList());
    }

    public List<Issue> findCompletedIssues() {
        return issueRepository.findDoneIssues();
    }

    public void moveToNextStatus(long issueId) {
        issueRepository.updateToNextStatus(issueId);
    }

    public void moveToPreviousStatus(long issueId) {
        issueRepository.updateToPreviousStatus(issueId);
    }

    public void completeIssue(long issueId) {
        issueRepository.updateToComplete(issueId);
    }

    public void undoIssue(long issueId) {
        issueRepository.updateToIncomplete(issueId);
    }

    public Issue findById(long issueId) {
        return issueRepository.findById(issueId).orElse(null);
    }

    @Transactional
    public void create(String summary, String description) {
        issueRepository.insert(new Issue(null, summary, description, IssueStatus.TODO, null));
    }

    @Transactional
    public void createWithAssignee(String summary, String description, Long assigneeId) {
        var assignee = assigneeId != null ? new Assignee(assigneeId, null, null) : null;
        var issue = new Issue(null, summary, description, IssueStatus.TODO, assignee);
        issueRepository.insert(issue);
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
