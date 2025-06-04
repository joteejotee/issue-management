package com.example.its.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * パスワードバリデーションアノテーション
 * - 8文字以上
 * - 半角英数字のみ
 * - 大文字・小文字・数字各1文字以上含む
 */
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Password {
    String message() default "パスワードは8文字以上で、大文字・小文字・数字を各1文字以上含む半角英数字である必要があります";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 