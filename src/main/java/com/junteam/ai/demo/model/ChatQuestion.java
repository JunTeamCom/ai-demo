package com.junteam.ai.demo.model;

import jakarta.validation.constraints.NotBlank;

public record ChatQuestion(
        @NotBlank(message = "标题不能为空") String title,
        @NotBlank(message = "问题不能为空") String question) {
}
