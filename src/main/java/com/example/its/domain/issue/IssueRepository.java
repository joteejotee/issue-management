package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IssueRepository {

    // "is_completed"がfalseの課題を全て取得するクエリ
    @Select("SELECT * FROM issues WHERE is_completed = false")
    List<IssueEntity> findAllIncompleteIssues();

    // "is_completed"がtrueの課題を全て取得するクエリ
    @Select("SELECT * FROM issues WHERE is_completed = true")
    List<IssueEntity> findAllCompletedIssues();

    // 課題IDに基づいて、その課題の完了状態をtrueに更新するクエリ
    @Update("UPDATE issues SET is_completed = CASE WHEN is_completed = true THEN false ELSE true END WHERE id = #{issueId}")
    void completeIssue(long issueId);

    // 指定されたIDの課題をデータベースから取得するクエリ
    @Select("SELECT * FROM issues WHERE id = #{issueId}")
    IssueEntity findById(long issueId);

    // 新しい課題の概要と説明をDBに新規挿入するクエリ
    @Insert("INSERT INTO issues (summary, description) VALUES (#{summary}, #{description})")
    void insert(String summary, String description);

    // 課題IDに基づいて、その課題を削除するクエリ
    @Update("DELETE FROM issues WHERE id = #{issueId}")
    void delete(long issueId);
}
