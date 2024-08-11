package com.example.its.web.issue;

import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor //finalが付いたフィールドを引数とするコンストラクタを自動生成するアノテ
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    public String showList(Model model) {

        model.addAttribute("issueList", issueService.findAll());

        //↓この行にBPはってデバッグし、右クリ→式の評価→model、でmodelに値がちゃんと入ってるのが確認できる。
        // てことはviewに値を渡す準備できてるってこと。
        return "issues/List";
    }

    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form) {
        return "issues/creationForm";
    }

    @PostMapping
    public String create(IssueForm form, Model model) {

        issueService.create(form.getSummary(), form.getDescription());
        return "redirect:/issues";
    }
}
