package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 60, message = "分类名称不能超过60个字符")
    private String name;

    @Size(max = 200, message = "分类说明不能超过200个字符")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
