package com.example.its.web.assignee;

import com.example.its.domain.assignee.AssigneeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/assignees")
@RequiredArgsConstructor
public class AssigneeController {

    private final AssigneeService assigneeService;

    @GetMapping
    public String list(Model model) {
        var assignees = assigneeService.findAll();
        model.addAttribute("assignees", assignees);
        return "assignees/list";
    }

    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute AssigneeForm form) {
        return "assignees/creationForm";
    }

    @PostMapping
    public String create(@Validated AssigneeForm form, BindingResult bindingResult) {
        log.info("=== 担当者作成処理開始 ===");
        log.info("フォームデータ: name={}, photoUrl={}", form.getName(), form.getPhotoUrl());
        log.info("バリデーションエラー数: {}", bindingResult.getErrorCount());
        
        if (bindingResult.hasErrors()) {
            log.error("バリデーションエラーが発生しました:");
            bindingResult.getAllErrors().forEach(error -> 
                log.error("- {}", error.getDefaultMessage())
            );
            return "assignees/creationForm";
        }
        
        try {
            log.info("担当者サービスのcreateメソッドを呼び出し中...");
            var createdAssignee = assigneeService.create(form.getName(), form.getPhotoUrl());
            log.info("担当者作成完了: id={}, name={}, photoUrl={}", 
                createdAssignee.getId(), createdAssignee.getName(), createdAssignee.getPhotoUrl());
        } catch (Exception e) {
            log.error("担当者作成中にエラーが発生しました", e);
            throw e;
        }
        
        log.info("担当者一覧画面にリダイレクト中...");
        return "redirect:/assignees";
    }

    @GetMapping("/{id}/editForm")
    public String showEditForm(@PathVariable long id, Model model) {
        var assignee = assigneeService.findById(id);
        if (assignee.isEmpty()) {
            return "redirect:/assignees";
        }
        var form = new AssigneeForm(assignee.get().getName(), assignee.get().getPhotoUrl());
        model.addAttribute("assigneeForm", form);
        model.addAttribute("assigneeId", id);
        return "assignees/editForm";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Validated AssigneeForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("assigneeId", id);
            return "assignees/editForm";
        }
        assigneeService.update(id, form.getName(), form.getPhotoUrl());
        return "redirect:/assignees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        assigneeService.delete(id);
        return "redirect:/assignees";
    }
} 