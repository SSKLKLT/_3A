package com.shop._3a.web;

import com.shop._3a.service.RankService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 排行榜：积分榜 / 活跃榜。
 */
@WebServlet("/rank")
public class RankServlet extends BaseServlet {

    private final RankService rankService = new RankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = param(req, "tab", "points");
        List<RankService.RankItem> items = "active".equals(tab)
                ? rankService.activeRank(20)
                : rankService.pointsRank(20);
        Map<String, Integer> overview = rankService.overview();

        req.setAttribute("tab", tab);
        req.setAttribute("items", items);
        req.setAttribute("overview", overview);
        forward(req, resp, "/WEB-INF/views/rank.jsp");
    }
}
