package com.shop._3a.web;

import com.shop._3a.model.User;
import com.shop._3a.service.CommentService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 发表评论（需登录），成功后回跳帖子详情。
 */
@WebServlet("/comment")
public class CommentServlet extends BaseServlet {

    private final CommentService commentService = new CommentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User author = currentUser(req);
        long postId = paramLong(req, "postId", 0);
        String content = param(req, "content", "");
        String back = req.getContextPath() + "/posts/detail?id=" + postId;
        try {
            commentService.create(author, postId, content);
        } catch (IllegalArgumentException e) {
            back += "&error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
        redirect(resp, back);
    }
}
