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
    public String showCreationForm(Model model) {
        model.addAttribute("assigneeForm", new AssigneeForm());
        return "assignees/creationForm";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute("assigneeForm") AssigneeForm form, BindingResult bindingResult, Model model) {
        log.debug("担当者作成処理を開始します - name: {}", form.getName());
        
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました: {}", bindingResult.getErrorCount());
            model.addAttribute("assigneeForm", form);
            return "assignees/creationForm";
        }
        
        try {
            var createdAssignee = assigneeService.create(form.getName(), form.getPhotoUrl());
            log.info("担当者作成が完了しました - id: {}, name: {}", 
                createdAssignee.getId(), createdAssignee.getName());
            return "redirect:/assignees";
        } catch (Exception e) {
            log.error("担当者作成中にエラーが発生しました - name: {}", form.getName(), e);
            model.addAttribute("assigneeForm", form);
            model.addAttribute("errorMessage", "担当者の作成中にエラーが発生しました。");
            return "assignees/creationForm";
        }
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
    public String update(@PathVariable long id, @Validated @ModelAttribute("assigneeForm") AssigneeForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("assigneeForm", form);
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