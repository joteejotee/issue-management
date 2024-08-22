package com.example.its.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data //todo @Entityに変えるかも
public class IssueEntity {
    private long id;
    private String summary;//課題概要
    private String description;//課題詳細
    //trueは完了、falseは未完了を表す。課題が完了しているかを示すフラグ
    private boolean isCompleted;
}