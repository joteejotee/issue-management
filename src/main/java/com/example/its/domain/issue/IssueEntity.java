package com.example.its.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data //todo @Entityに変えるかも
public class IssueEntity {
    private long id;
    private String summary;//課題概要
    private String description;//課題詳細
    private IssueStatus status; // 課題のステータス：TODO, DOING, DONE

    // 後方互換性のためのヘルパーメソッド
    public boolean isCompleted() {
        return status == IssueStatus.DONE;
    }

    public boolean isTodo() {
        return status == IssueStatus.TODO;
    }

    public boolean isDoing() {
        return status == IssueStatus.DOING;
    }

    public boolean isDone() {
        return status == IssueStatus.DONE;
    }
}