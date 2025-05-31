package com.example.its.domain.assignee;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AssigneeRepository {

    @Select("SELECT id, name, photo_url AS photoUrl FROM assignees ORDER BY id")
    List<AssigneeEntity> findAll();

    @Select("SELECT id, name, photo_url AS photoUrl FROM assignees WHERE id = #{id}")
    Optional<AssigneeEntity> findById(long id);

    @Insert("INSERT INTO assignees (name, photo_url) VALUES (#{name}, #{photoUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AssigneeEntity assignee);

    @Update("UPDATE assignees SET name = #{name}, photo_url = #{photoUrl} WHERE id = #{id}")
    void update(AssigneeEntity assignee);

    @Delete("DELETE FROM assignees WHERE id = #{id}")
    void delete(long id);
} 