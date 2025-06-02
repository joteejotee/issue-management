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

    public List<IssueEntity> findTodoIssues() {
        return issueRepository.findTodoIssues();
    }

    public List<IssueEntity> findDoingIssues() {
        return issueRepository.findDoingIssues();
    }

    public List<IssueEntity> findDoneIssues() {
        return issueRepository.findDoneIssues();
    }

    public List<IssueEntity> findIncompleteIssues() {
        var todoIssues = issueRepository.findTodoIssues();
        var doingIssues = issueRepository.findDoingIssues();
        return Stream.concat(todoIssues.stream(), doingIssues.stream())
                .collect(Collectors.toList());
    }

    public List<IssueEntity> findCompletedIssues() {
        return issueRepository.findDoneIssues();
    }

    public void moveToNextStatus(long issueId) {
        issueRepository.updateToNextStatus(issueId);
    }

    public void moveToPreviousStatus(long issueId) {
        issueRepository.updateToPreviousStatus(issueId);
    }

    public void completeIssue(long issueId) {
        var issue = issueRepository.findById(issueId);
        if (issue.isPresent() && issue.get().getStatus() == IssueStatus.DONE) {
            issueRepository.updateToPreviousStatus(issueId);
            issueRepository.updateToPreviousStatus(issueId);
        } else {
            while (true) {
                var currentIssue = issueRepository.findById(issueId);
                if (currentIssue.isPresent() && currentIssue.get().getStatus() == IssueStatus.DONE) {
                    break;
                }
                issueRepository.updateToNextStatus(issueId);
            }
        }
    }

    public IssueEntity findById(long issueId) {
        return issueRepository.findById(issueId).orElse(null);
    }

    @Transactional
    public void create(String summary, String description) {
        IssueEntity issue = new IssueEntity(0L, summary, description, IssueStatus.TODO, null);
        issueRepository.insert(issue);
    }

    @Transactional
    public void createWithAssignee(String summary, String description, Long assigneeId) {
        IssueEntity issue = new IssueEntity(0L, summary, description, IssueStatus.TODO, assigneeId);
        issueRepository.insert(issue);
    }

    @Transactional
    public void updateAssignee(Long issueId, Long assigneeId) {
        issueRepository.updateAssignee(issueId, assigneeId);
    }

    @Transactional
    public void delete(Long id) {
        issueRepository.delete(id);
    }
}
