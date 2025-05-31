package com.example.its.domain.assignee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.info("=== AssigneeService.create 開始 ===");
        log.info("パラメータ: name={}, photoUrl={}", name, photoUrl);
        
        var assignee = new AssigneeEntity(0, name, photoUrl);
        log.info("作成する担当者エンティティ: {}", assignee);
        
        try {
            log.info("データベースにINSERT実行中...");
            assigneeRepository.insert(assignee);
            log.info("INSERT完了後の担当者エンティティ: {}", assignee);
            log.info("生成されたID: {}", assignee.getId());
            
            return assignee;
        } catch (Exception e) {
            log.error("データベースINSERT中にエラーが発生", e);
            throw e;
        }
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