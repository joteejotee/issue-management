package com.example.its.domain.issue;

/**
 * 課題のステータスを表すEnum
 */
public enum IssueStatus {
    TODO("課題"),
    DOING("作業中"),
    DONE("完了");

    private final String displayName;

    IssueStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 文字列からIssueStatusを取得
     */
    public static IssueStatus fromString(String status) {
        if (status == null) {
            return TODO; // デフォルト値
        }
        try {
            return IssueStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TODO; // 無効な値の場合はデフォルト値
        }
    }
} 