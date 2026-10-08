package com.example.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookRequest {

    @NotBlank(message = "书名不能为空")
    @Size(max = 100, message = "书名不能超过100个字符")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 80, message = "作者不能超过80个字符")
    private String author;

    @Size(max = 32, message = "ISBN不能超过32个字符")
    private String isbn;

    @Size(max = 100, message = "出版社不能超过100个字符")
    private String publisher;

    @Min(value = 1000, message = "出版年份不能早于1000年")
    @Max(value = 2100, message = "出版年份不能晚于2100年")
    private Integer publishYear;

    @NotNull(message = "馆藏数量不能为空")
    @Min(value = 0, message = "馆藏数量不能为负数")
    private Integer totalCopies;

    @NotNull(message = "可借数量不能为空")
    @Min(value = 0, message = "可借数量不能为负数")
    private Integer availableCopies;

    private Long categoryId;

    @Size(max = 80, message = "馆藏位置不能超过80个字符")
    private String shelfLocation;

    @Size(max = 500, message = "图书简介不能超过500个字符")
    private String summary;

    @Size(max = 255, message = "封面地址不能超过255个字符")
    private String coverUrl;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public Integer getPublishYear() { return publishYear; }
    public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
