package com.shop._3a.web;

import com.shop._3a.model.Post;
import com.shop._3a.model.PostCard;
import com.shop._3a.service.CommentService;
import com.shop._3a.service.PointService;
import com.shop._3a.service.PostService;
import com.shop._3a.service.RankService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 帖子列表（社区首页）：支持关键词搜索、板块筛选、最新/热门排序与分页。
 */
@WebServlet("/posts")
public class PostListServlet extends BaseServlet {

    private static final int PAGE_SIZE = 8;

    private final PostService postService = new PostService();
    private final CommentService commentService = new CommentService();
    private final RankService rankService = new RankService();
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = param(req, "keyword", "");
        String category = param(req, "category", "");
        String sort = param(req, "sort", "new");
        int page = Math.max(1, paramInt(req, "page", 1));

        List<PostCard> matched = postService.cards(keyword, category, sort);
        int total = matched.size();
        int totalPages = Math.max(1, (int) Math.ceil(total * 1.0 / PAGE_SIZE));
        if (page > totalPages) {
            page = totalPages;
        }
        int from = total == 0 ? 0 : Math.min((page - 1) * PAGE_SIZE, total - 1);
        int to = Math.min(from + PAGE_SIZE, total);
        List<PostCard> pageList = total == 0 ? List.of() : matched.subList(from, to);

        req.setAttribute("cards", pageList);
        req.setAttribute("keyword", keyword);
        req.setAttribute("category", category);
        req.setAttribute("sort", sort);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total", total);
        req.setAttribute("categories", Post.CATEGORIES);
        req.setAttribute("overview", rankService.overview());
        req.setAttribute("topUsers", rankService.pointsRank(5));
        req.setAttribute("recentComments", commentService.recent(5));

        com.shop._3a.model.User me = currentUser(req);
        req.setAttribute("signedToday", me != null && pointService.hasSignedInToday(me.getId()));

        forward(req, resp, "/WEB-INF/views/posts.jsp");
    }
}
