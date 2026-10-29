package com.shop._3a.web;

import com.shop._3a.model.Post;
import com.shop._3a.service.CommentService;
import com.shop._3a.service.PostService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 帖子详情：浏览量 +1，并展示评论区。
 */
@WebServlet("/posts/detail")
public class PostDetailServlet extends BaseServlet {

    private final PostService postService = new PostService();
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = paramLong(req, "id", 0);
        Post post = postService.view(id);
        if (post == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute("message", "帖子不存在或已被删除");
            forward(req, resp, "/WEB-INF/views/error.jsp");
            return;
        }
        req.setAttribute("post", post);
        req.setAttribute("comments", commentService.listOf(id));
        req.setAttribute("commentCount", commentService.countOf(id));
        forward(req, resp, "/WEB-INF/views/post_detail.jsp");
    }
}
