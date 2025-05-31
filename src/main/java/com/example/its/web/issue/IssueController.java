package com.example.its.web.issue;

import com.example.its.domain.assignee.AssigneeService;
import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor //finalが付いたフィールドを引数とするコンストラクタを自動生成するアノテーション
public class IssueController {
    private final IssueService issueService;
    private final AssigneeService assigneeService;

    // 課題一覧を表示する
    @GetMapping
    public String listIssues(Model model) {
        // 各ステータス別の課題リストをモデルに追加し、ビューに渡す
        model.addAttribute("todoIssues", issueService.findAllTodoIssues());
        model.addAttribute("doingIssues", issueService.findAllDoingIssues());
        model.addAttribute("doneIssues", issueService.findAllDoneIssues());
        
        // 後方互換性のために既存の属性も追加
        model.addAttribute("incompleteIssues", issueService.findAllIncompleteIssues());
        model.addAttribute("completedIssues", issueService.findAllCompletedIssues());
        
        // 担当者一覧も追加
        model.addAttribute("assignees", assigneeService.findAll());
        
        return "issues/list"; // 課題リストのビューを返す
    }

    // 課題作成フォームを表示する
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form, Model model) {
        // 担当者一覧をフォームに渡す
        model.addAttribute("assignees", assigneeService.findAll());
        return "issues/creationForm";
    }

    // 指定されたIDの課題の詳細をモデルに追加し、ビューに渡す
    @GetMapping("/{id}")
    public String getIssueDetails(@PathVariable Long id, Model model) {
        var issue = issueService.findById(id);
        model.addAttribute("issue", issue);
        
        // 担当者情報も追加
        if (issue.getAssigneeId() != null) {
            var assignee = assigneeService.findById(issue.getAssigneeId());
            assignee.ifPresent(a -> model.addAttribute("assignee", a));
        }
        
        return "issues/detail"; // 課題の詳細ビューを返す
    }

    // 課題のステータスを次の段階に進める
    @PostMapping("/next")
    public String moveToNextStatus(@RequestParam Long id) {
        issueService.moveToNextStatus(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // 課題のステータスを前の段階に戻す
    @PostMapping("/previous")
    public String moveToPreviousStatus(@RequestParam Long id) {
        issueService.moveToPreviousStatus(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // 後方互換性のための既存のエンドポイント
    @PostMapping("/complete")
    public String completeIssue(@RequestParam Long id) {
        issueService.completeIssue(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // フォームから受け取った課題を作成し、データベースに保存する(既存のメソッドをそのまま保持)
    @PostMapping
    public String createIssue(@ModelAttribute IssueForm form) {
        if (form.getAssigneeId() != null) {
            issueService.insertWithAssignee(form.getSummary(), form.getDescription(), form.getAssigneeId());
        } else {
            issueService.insert(form.getSummary(), form.getDescription());
        }
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // 指定されたIDの課題を削除する
    @PostMapping("/delete")
    public String deleteIssue(@RequestParam Long id) {
        issueService.delete(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }
}
