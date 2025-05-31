package com.example.its.domain.assignee;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AssigneeEntity {
    private long id;
    private String name;        // 担当者名
    private String photoUrl;    // 写真ファイルのURL/パス
} 