package com.shop._3a.model;

/**
 * 列表页用的帖子卡片视图对象：帖子 + 评论数 + 热门标记。
 */
public class PostCard {

    private Post post;
    private int commentCount;
    private boolean hot;
    private String authorAvatar = "🎮";

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
}
