package com.shop._3a.web;

import com.shop._3a.service.AiGuideService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * AI 攻略（占位）：GET 展示查询表单，POST 返回占位攻略结构。
 * <p>
 * 后续接入真实大模型时，仅需替换 {@link AiGuideService#generate(String, String)} 实现。
 */
@WebServlet("/ai-guide")
public class AiGuideServlet extends BaseServlet {

    private final AiGuideService aiGuideService = new AiGuideService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("hotGames", List.of("艾尔登法环", "塞尔达传说：王国之泪", "博德之门 3", "荒野大镖客 2", "黑神话：悟空"));
        forward(req, resp, "/WEB-INF/views/ai_guide.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) == null) {
            redirect(resp, req.getContextPath() + "/login?redirect=" + req.getContextPath() + "/ai-guide");
            return;
        }
        String game = param(req, "game", "");
        String focus = param(req, "focus", "");
        if (game.isEmpty()) {
            req.setAttribute("error", "请填写游戏名称");
            doGet(req, resp);
            return;
        }
        req.setAttribute("guide", aiGuideService.generate(game, focus));
        req.setAttribute("game", game);
        req.setAttribute("focus", focus);
        doGet(req, resp);
    }
}
