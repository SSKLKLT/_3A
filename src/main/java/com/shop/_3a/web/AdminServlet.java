package com.shop._3a.web;

import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.service.CommentService;
import com.shop._3a.service.PostService;
import com.shop._3a.service.RankService;
import com.shop._3a.store.DataStore;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 后台管理：数据概览与内容审核（删除帖子 / 评论），仅管理员可访问。
 */
@WebServlet("/admin")
public class AdminServlet extends BaseServlet {

    private final PostService postService = new PostService();
    private final CommentService commentService = new CommentService();
    private final RankService rankService = new RankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User operator = currentUser(req);
        if (operator == null || !operator.isAdmin()) {
            req.setAttribute("message", "仅管理员可访问后台");
            forward(req, resp, "/WEB-INF/views/error.jsp");
            return;
        }
        Map<String, Integer> overview = rankService.overview();
        req.setAttribute("overview", overview);
        req.setAttribute("posts", DataStore.getInstance().allPosts());
        req.setAttribute("users", DataStore.getInstance().allUsers());
        req.setAttribute("comments", DataStore.getInstance().allComments());
        forward(req, resp, "/WEB-INF/views/admin.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User operator = currentUser(req);
        String action = param(req, "action", "");
        String tip = "操作完成";
        if (operator == null || !operator.isAdmin()) {
            tip = "无权限";
        } else if ("deletePost".equals(action)) {
            long postId = paramLong(req, "postId", 0);
            tip = postService.delete(postId, operator) ? "帖子已删除" : "删除失败";
        } else if ("deleteComment".equals(action)) {
            long postId = paramLong(req, "postId", 0);
            long commentId = paramLong(req, "commentId", 0);
            tip = commentService.delete(postId, commentId, operator) ? "评论已删除" : "删除失败";
        } else if ("pinPost".equals(action)) {
            Post post = DataStore.getInstance().findPost(paramLong(req, "postId", 0));
            if (post != null) {
                post.setPinned(!post.isPinned());
                DataStore.getInstance().savePost(post);
                tip = post.isPinned() ? "已置顶" : "已取消置顶";
            } else {
                tip = "帖子不存在";
            }
        }
        redirect(resp, req.getContextPath() + "/admin?tip="
                + java.net.URLEncoder.encode(tip, java.nio.charset.StandardCharsets.UTF_8));
    }
}
