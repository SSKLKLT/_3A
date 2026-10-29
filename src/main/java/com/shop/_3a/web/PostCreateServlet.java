package com.shop._3a.web;

import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.service.PostService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 发帖：GET 展示编辑器，POST 提交并跳转详情页。
 */
@WebServlet("/posts/new")
public class PostCreateServlet extends BaseServlet {

    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", Post.CATEGORIES);
        forward(req, resp, "/WEB-INF/views/post_new.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User author = currentUser(req);
        String title = param(req, "title", "");
        String content = param(req, "content", "");
        String category = param(req, "category", "讨论");
        req.setAttribute("title", title);
        req.setAttribute("content", content);
        req.setAttribute("category", category);
        req.setAttribute("categories", Post.CATEGORIES);
        try {
            Post post = postService.create(author, title, content, category);
            redirect(resp, req.getContextPath() + "/posts/detail?id=" + post.getId() + "&created=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            forward(req, resp, "/WEB-INF/views/post_new.jsp");
        }
    }
}
