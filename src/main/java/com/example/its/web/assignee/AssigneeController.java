package com.example.its.web.assignee;

import com.example.its.domain.assignee.AssigneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        if (bindingResult.hasErrors()) {
            return "assignees/creationForm";
        }
        assigneeService.create(form.getName(), form.getPhotoUrl());
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