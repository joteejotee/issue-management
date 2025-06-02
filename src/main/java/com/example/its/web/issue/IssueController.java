package com.example.its.web.issue;

import com.example.its.domain.assignee.AssigneeService;
import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor //finalが付いたフィールドを引数とするコンストラクタを自動生成するアノテーション
public class IssueController {
    private final IssueService issueService;
    private final AssigneeService assigneeService;

    // 課題一覧を表示する
    @GetMapping
    public String list(Model model) {
        var todoIssues = issueService.findTodoIssues();
        var doingIssues = issueService.findDoingIssues();
        var doneIssues = issueService.findDoneIssues();
        var assignees = assigneeService.findAll();

        model.addAttribute("todoIssues", todoIssues);
        model.addAttribute("doingIssues", doingIssues);
        model.addAttribute("doneIssues", doneIssues);
        model.addAttribute("assignees", assignees);

        return "issues/list";
    }

    // 課題作成フォームを表示する
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form, Model model) {
        var assignees = assigneeService.findAll();
        model.addAttribute("assignees", assignees);
        return "issues/creationForm";
    }

    // 指定されたIDの課題の詳細をモデルに追加し、ビューに渡す
    @GetMapping("/{issueId}")
    public String show(@PathVariable("issueId") long issueId, Model model) {
        var issue = issueService.findById(issueId);
        model.addAttribute("issue", issue);
        return "issues/show";
    }

    // 課題のステータスを次の段階に進める
    @PostMapping("/next")
    public String next(@RequestParam("id") long issueId) {
        issueService.moveToNextStatus(issueId);
        return "redirect:/issues";
    }

    // 課題のステータスを前の段階に戻す
    @PostMapping("/previous")
    public String previous(@RequestParam("id") long issueId) {
        issueService.moveToPreviousStatus(issueId);
        return "redirect:/issues";
    }

    // 後方互換性のための既存のエンドポイント
    @PostMapping("/complete")
    public String completeIssue(@RequestParam Long id) {
        issueService.completeIssue(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // フォームから受け取った課題を作成し、データベースに保存する(既存のメソッドをそのまま保持)
    @PostMapping
    public String create(@Validated IssueForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var assignees = assigneeService.findAll();
            model.addAttribute("assignees", assignees);
            return "issues/creationForm";
        }
        issueService.createWithAssignee(form.getSummary(), form.getDescription(), form.getAssigneeId());
        return "redirect:/issues";
    }

    // 指定されたIDの課題を削除する
    @PostMapping("/{issueId}/delete")
    public String delete(@PathVariable("issueId") long issueId) {
        issueService.delete(issueId);
        return "redirect:/issues";
    }
}
