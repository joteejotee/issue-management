package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IssueRepository {

    // TODO状態の課題を全て取得するクエリ
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE status = 'TODO'")
    List<IssueEntity> findAllTodoIssues();

    // DOING状態の課題を全て取得するクエリ
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE status = 'DOING'")
    List<IssueEntity> findAllDoingIssues();

    // DONE状態の課題を全て取得するクエリ
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE status = 'DONE'")
    List<IssueEntity> findAllDoneIssues();

    // 後方互換性のためのメソッド（未完了 = TODO + DOING）
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE status IN ('TODO', 'DOING')")
    List<IssueEntity> findAllIncompleteIssues();

    // 後方互換性のためのメソッド（完了 = DONE）
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE status = 'DONE'")
    List<IssueEntity> findAllCompletedIssues();

    // 課題のステータスを次の段階に進めるクエリ
    @Update("UPDATE issues SET status = " +
            "CASE " +
            "WHEN status = 'TODO' THEN 'DOING' " +
            "WHEN status = 'DOING' THEN 'DONE' " +
            "WHEN status = 'DONE' THEN 'TODO' " +
            "END " +
            "WHERE id = #{issueId}")
    void moveToNextStatus(long issueId);

    // 課題のステータスを前の段階に戻すクエリ
    @Update("UPDATE issues SET status = " +
            "CASE " +
            "WHEN status = 'DONE' THEN 'DOING' " +
            "WHEN status = 'DOING' THEN 'TODO' " +
            "WHEN status = 'TODO' THEN 'DONE' " +
            "END " +
            "WHERE id = #{issueId}")
    void moveToPreviousStatus(long issueId);

    // 指定されたIDの課題をデータベースから取得するクエリ
    @Select("SELECT id, summary, description, status, assignee_id AS assigneeId FROM issues WHERE id = #{issueId}")
    IssueEntity findById(long issueId);

    // 新しい課題の概要と説明をDBに新規挿入するクエリ（デフォルトでTODO状態）
    @Insert("INSERT INTO issues (summary, description, status) VALUES (#{summary}, #{description}, 'TODO')")
    void insert(String summary, String description);

    // 担当者付きで新しい課題を挿入
    @Insert("INSERT INTO issues (summary, description, status, assignee_id) VALUES (#{summary}, #{description}, 'TODO', #{assigneeId})")
    void insertWithAssignee(String summary, String description, Long assigneeId);

    // 課題の担当者を更新
    @Update("UPDATE issues SET assignee_id = #{assigneeId} WHERE id = #{issueId}")
    void updateAssignee(long issueId, Long assigneeId);

    // 課題IDに基づいて、その課題を削除するクエリ
    @Update("DELETE FROM issues WHERE id = #{issueId}")
    void delete(long issueId);
}
