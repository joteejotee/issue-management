package com.example.its.web.issue;

import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor //finalが付いたフィールドを引数とするコンストラクタを自動生成するアノテ
public class IssueController {
    private final IssueService issueService;

    // 課題一覧を表示する
    @GetMapping
    public String listIssues(Model model) {
        // 未完了の課題リストをモデルに追加し、ビューに渡す
        model.addAttribute("incompleteIssues", issueService.findAllIncompleteIssues());
        // 完了した課題リストをモデルに追加し、ビューに渡す
        model.addAttribute("completedIssues", issueService.findAllCompletedIssues());
        //↓この行にBPはってデバッグし、右クリ→式の評価→model、でmodelに値がちゃんと入ってるのが確認できる。
        // てことはviewに値を渡す準備できてるってこと。
        return "issues/list"; // 課題リストのビューを返す
    }

    // 課題作成フォームを表示する
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form) {
        return "issues/creationForm";
    }

    // 指定されたIDの課題の詳細をモデルに追加し、ビューに渡す
    @GetMapping("/{id}")
    public String getIssueDetails(@PathVariable Long id, Model model) {
        model.addAttribute("issue", issueService.findById(id));
        return "issues/detail"; // 課題の詳細ビューを返す
    }

    @PostMapping("/complete")
    public String completeIssue(@RequestParam Long id) {
        issueService.completeIssue(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // フォームから受け取った課題を作成し、データベースに保存する(既存のメソッドをそのまま保持)
    @PostMapping
    public String createIssue(@ModelAttribute IssueForm form) {
        issueService.insert(form.getSummary(), form.getDescription());
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }

    // 指定されたIDの課題を削除する
    @PostMapping("/delete")
    public String deleteIssue(@RequestParam Long id) {
        issueService.delete(id);
        return "redirect:/issues"; // 課題一覧にリダイレクトする
    }
}
