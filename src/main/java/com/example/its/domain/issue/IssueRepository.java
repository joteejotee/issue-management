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

    @Select("SELECT id, summary, description, status, assignee_id FROM issues WHERE status = 'TODO' ORDER BY id")
    List<IssueEntity> findTodoIssues();

    @Select("SELECT id, summary, description, status, assignee_id FROM issues WHERE status = 'DOING' ORDER BY id")
    List<IssueEntity> findDoingIssues();

    @Select("SELECT id, summary, description, status, assignee_id FROM issues WHERE status = 'DONE' ORDER BY id")
    List<IssueEntity> findDoneIssues();

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

    @Select("SELECT id, summary, description, status, assignee_id FROM issues WHERE id = #{issueId}")
    Optional<IssueEntity> findById(long issueId);

    @Insert("INSERT INTO issues (summary, description, status, assignee_id) VALUES (#{summary}, #{description}, 'TODO', #{assigneeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(IssueEntity issue);

    @Update("UPDATE issues SET assignee_id = #{assigneeId} WHERE id = #{issueId}")
    void updateAssignee(long issueId, Long assigneeId);

    @Update("DELETE FROM issues WHERE id = #{issueId}")
    void delete(long issueId);
}
