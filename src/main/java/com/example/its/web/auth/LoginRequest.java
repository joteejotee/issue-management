package com.example.its.web.auth;

import com.example.its.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ログインリクエストDTO
 * フロントエンドとバックエンドで同じバリデーションルールを適用
 */
public record LoginRequest(
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式で入力してください")
    String email,
    
    @NotBlank(message = "パスワードは必須です")
    @Password
    String password
) {} 