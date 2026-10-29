package com.shop._3a.model;

import java.time.LocalDateTime;

/**
 * 社区帖子。
 */
public class Post {

    /** 板块：攻略 / 评测 / 讨论 / 闲聊 */
    public static final String[] CATEGORIES = {"攻略", "评测", "讨论", "闲聊"};

    private long id;
    private long authorId;
    private String authorName;
    private String title;
    private String content;
    private String category = "讨论";
    private int viewCount;
    private boolean pinned;
    private LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** 列表页摘要 */
    public String getSummary() {
        String plain = content == null ? "" : content.replaceAll("\\s+", " ").trim();
        return plain.length() <= 90 ? plain : plain.substring(0, 90) + "…";
    }
}
