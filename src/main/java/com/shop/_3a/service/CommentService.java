package com.shop._3a.service;

import com.shop._3a.model.Comment;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 评论服务：发表评论、按帖子查询、删除。
 */
public class CommentService {

    private final DataStore store;
    private final PointService pointService;

    public CommentService() {
        this(DataStore.getInstance(), new PointService());
    }

    public CommentService(DataStore store, PointService pointService) {
        this.store = store;
        this.pointService = pointService;
    }

    /**
     * 发表评论，成功后发放积分。
     *
     * @throws IllegalArgumentException 内容不合法或帖子不存在
     */
    public Comment create(User author, long postId, String content) {
        if (author == null) {
            throw new IllegalArgumentException("请先登录");
        }
        String c = content == null ? "" : content.trim();
        if (c.length() < 2 || c.length() > 500) {
            throw new IllegalArgumentException("评论长度需为 2-500 字");
        }
        if (store.findPost(postId) == null) {
            throw new IllegalArgumentException("帖子不存在或已被删除");
        }
        Comment comment = store.addComment(author.getId(), postId, c);
        if (comment == null) {
            throw new IllegalArgumentException("评论失败，请稍后重试");
        }
        pointService.grantCommentBonus(author.getId());
        return comment;
    }

    public List<Comment> listOf(long postId) {
        return store.commentsOf(postId);
    }

    public int countOf(long postId) {
        return store.countCommentsOf(postId);
    }

    /** 全站最新评论，用于侧栏展示 */
    public List<Comment> recent(int limit) {
        List<Comment> all = store.allComments();
        all.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        if (limit <= 0 || all.size() <= limit) {
            return all;
        }
        return new ArrayList<>(all.subList(0, limit));
    }

    /** 删除评论，仅评论作者本人或管理员可操作 */
    public boolean delete(long postId, long commentId, User operator) {
        if (operator == null) {
            return false;
        }
        if (operator.isAdmin()) {
            return store.deleteComment(postId, commentId);
        }
        for (Comment comment : store.commentsOf(postId)) {
            if (comment.getId() == commentId) {
                if (comment.getAuthorId() != operator.getId()) {
                    return false;
                }
                return store.deleteComment(postId, commentId);
            }
        }
        return false;
    }
}
