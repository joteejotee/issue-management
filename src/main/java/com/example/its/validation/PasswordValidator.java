package com.example.its.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * パスワードバリデーターの実装
 * フロントエンド（Zod）と同じルールでサーバーサイドバリデーションを実行
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    
    /**
     * パスワードルール:
     * - 8文字以上
     * - 半角英数字のみ [A-Za-z0-9]
     * - 大文字最低1文字 (?=.*[A-Z])
     * - 小文字最低1文字 (?=.*[a-z])
     * - 数字最低1文字 (?=.*\d)
     */
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Override
    public void initialize(Password constraintAnnotation) {
        // 初期化処理は不要
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(value).matches();
    }
} 