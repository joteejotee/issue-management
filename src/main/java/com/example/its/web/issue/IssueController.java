package com.example.its.web.issue;

import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor //finalが付いたフィールドを引数とするコンストラクタを自動生成するアノテ
public class IssueController {
    private final IssueService issueService;

    @GetMapping("/issues")
    public String showList(Model model) {

        model.addAttribute("issueList", issueService.findAll());

        //↓この行にBPはってデバッグし、右クリ→式の評価→model、でmodelに値がちゃんと入ってるのが確認できる。
        // てことはviewに値を渡す準備できてるってこと。
        return "issues/List";
    }
}
