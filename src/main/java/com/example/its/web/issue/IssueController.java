package com.example.its.web.issue;

import com.example.its.domain.assignee.AssigneeService;
import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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
    public String showCreationForm(Model model) {
        try {
            var assignees = assigneeService.findAll();
            model.addAttribute("assignees", assignees);
            model.addAttribute("issueForm", new IssueForm());
        } catch (Exception e) {
            log.error("課題作成フォーム表示中にエラーが発生しました", e);
            // エラーが発生した場合でも空のリストを設定
            model.addAttribute("assignees", java.util.Collections.emptyList());
            model.addAttribute("issueForm", new IssueForm());
        }
        return "issues/creationForm";
    }

    // 指定されたIDの課題の詳細をモデルに追加し、ビューに渡す
    @GetMapping("/{issueId}")
    public String show(@PathVariable("issueId") long issueId, Model model) {
        var issue = issueService.findById(issueId);
        if (issue == null) {
            log.warn("存在しない課題IDが指定されました: {}", issueId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "課題が見つかりません");
        }
        model.addAttribute("issue", issue);
        return "issues/detail";
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

    // フォームから受け取った課題を作成し、データベースに保存する
    @PostMapping
    public String create(@Validated @ModelAttribute("issueForm") IssueForm form, BindingResult bindingResult, Model model) {
        log.debug("課題作成処理を開始します - summary: {}", form.getSummary());
        
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました: {}", bindingResult.getErrorCount());
            var assignees = assigneeService.findAll();
            model.addAttribute("assignees", assignees);
            model.addAttribute("issueForm", form);
            return "issues/creationForm";
        }
        
        try {
            issueService.createWithAssignee(form.getSummary(), form.getDescription(), form.getAssigneeId());
            log.info("課題作成が完了しました - summary: {}", form.getSummary());
            return "redirect:/issues";
        } catch (Exception e) {
            log.error("課題作成処理でエラーが発生しました - summary: {}", form.getSummary(), e);
            var assignees = assigneeService.findAll();
            model.addAttribute("assignees", assignees);
            model.addAttribute("issueForm", form);
            model.addAttribute("errorMessage", "課題の作成中にエラーが発生しました。");
            return "issues/creationForm";
        }
    }

    // 指定されたIDの課題を削除する
    @PostMapping("/{issueId}/delete")
    public String delete(@PathVariable("issueId") long issueId) {
        issueService.delete(issueId);
        return "redirect:/issues";
    }
}
