package com.example.its.web.issue;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class IssueForm {

    @NotBlank
    @Size(max=256)
    private String summary;

    @NotBlank
    @Size(max=256)
    private String description;
    
    private Long assigneeId; // 担当者ID（オプション）
}
