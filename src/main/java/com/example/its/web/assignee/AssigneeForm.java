package com.example.its.web.assignee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeForm {

    @NotBlank(message = "担当者名を入力してください")
    @Size(max = 100, message = "担当者名は100文字以内で入力してください")
    private String name;

    @NotBlank(message = "写真URLを入力してください")
    @Size(max = 256, message = "写真URLは256文字以内で入力してください")
    private String photoUrl;
} 