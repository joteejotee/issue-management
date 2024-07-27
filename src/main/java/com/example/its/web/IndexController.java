package com.example.its.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // GET /
    @GetMapping //("/")は省略可　//これでリクエストとハンドラーメソッドを紐づけ
    //@ResponseBody //以下がハンドラーメソッドと指定（メソッド内に直接表示内容を書く場合に書くアノテーション）
    public String index() {
        return "index";//index.htmlを返すって意味
    }
}
