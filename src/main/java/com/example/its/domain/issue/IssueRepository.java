package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Options;

import java.util.List;
import java.util.Optional;

@Mapper
public interface IssueRepository {

    @Select("SELECT issues.id, issues.summary, issues.description, issues.status, assignees.id as assignee_id, assignees.name as assignee_name, assignees.photo_url as assignee_photo_url FROM issues LEFT JOIN assignees ON issues.assignee_id = assignees.id WHERE issues.status = 'TODO' ORDER BY issues.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "assignee.id", column = "assignee_id"),
        @Result(property = "assignee.name", column = "assignee_name"),
        @Result(property = "assignee.photoUrl", column = "assignee_photo_url")
    })
    List<Issue> findTodoIssues();

    @Select("SELECT issues.id, issues.summary, issues.description, issues.status, assignees.id as assignee_id, assignees.name as assignee_name, assignees.photo_url as assignee_photo_url FROM issues LEFT JOIN assignees ON issues.assignee_id = assignees.id WHERE issues.status = 'DOING' ORDER BY issues.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "assignee.id", column = "assignee_id"),
        @Result(property = "assignee.name", column = "assignee_name"),
        @Result(property = "assignee.photoUrl", column = "assignee_photo_url")
    })
    List<Issue> findDoingIssues();

    @Select("SELECT issues.id, issues.summary, issues.description, issues.status, assignees.id as assignee_id, assignees.name as assignee_name, assignees.photo_url as assignee_photo_url FROM issues LEFT JOIN assignees ON issues.assignee_id = assignees.id WHERE issues.status = 'DONE' ORDER BY issues.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "assignee.id", column = "assignee_id"),
        @Result(property = "assignee.name", column = "assignee_name"),
        @Result(property = "assignee.photoUrl", column = "assignee_photo_url")
    })
    List<Issue> findDoneIssues();

    @Update("UPDATE issues SET status = CASE " +
            "WHEN status = 'TODO' THEN 'DOING' " +
            "WHEN status = 'DOING' THEN 'DONE' " +
            "ELSE status END " +
            "WHERE id = #{issueId}")
    void updateToNextStatus(long issueId);

    @Update("UPDATE issues SET status = CASE " +
            "WHEN status = 'DONE' THEN 'DOING' " +
            "WHEN status = 'DOING' THEN 'TODO' " +
            "ELSE status END " +
            "WHERE id = #{issueId}")
    void updateToPreviousStatus(long issueId);

    @Select("SELECT issues.id, issues.summary, issues.description, issues.status, assignees.id as assignee_id, assignees.name as assignee_name, assignees.photo_url as assignee_photo_url FROM issues LEFT JOIN assignees ON issues.assignee_id = assignees.id WHERE issues.id = #{issueId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "assignee.id", column = "assignee_id"),
        @Result(property = "assignee.name", column = "assignee_name"),
        @Result(property = "assignee.photoUrl", column = "assignee_photo_url")
    })
    Optional<Issue> findById(long issueId);

    @Insert("INSERT INTO issues (summary, description, status, assignee_id) VALUES (#{summary}, #{description}, 'TODO', #{assigneeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Issue issue);

    // 課題の担当者を更新
    @Update("UPDATE issues SET assignee_id = #{assigneeId} WHERE id = #{issueId}")
    void updateAssignee(long issueId, Long assigneeId);

    // 課題IDに基づいて、その課題を削除するクエリ
    @Update("DELETE FROM issues WHERE id = #{issueId}")
    void delete(long issueId);
}
