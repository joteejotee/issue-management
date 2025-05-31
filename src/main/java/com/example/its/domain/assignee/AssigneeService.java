package com.example.its.domain.assignee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssigneeService {

    private final AssigneeRepository assigneeRepository;

    /**
     * すべての担当者を取得
     */
    public List<AssigneeEntity> findAll() {
        return assigneeRepository.findAll();
    }

    /**
     * IDで担当者を取得
     */
    public Optional<AssigneeEntity> findById(long id) {
        return assigneeRepository.findById(id);
    }

    /**
     * 担当者を作成
     */
    @Transactional
    public AssigneeEntity create(String name, String photoUrl) {
        var assignee = new AssigneeEntity(0, name, photoUrl);
        assigneeRepository.insert(assignee);
        return assignee;
    }

    /**
     * 担当者を更新
     */
    @Transactional
    public void update(long id, String name, String photoUrl) {
        var assignee = new AssigneeEntity(id, name, photoUrl);
        assigneeRepository.update(assignee);
    }

    /**
     * 担当者を削除
     */
    @Transactional
    public void delete(long id) {
        assigneeRepository.delete(id);
    }
} 