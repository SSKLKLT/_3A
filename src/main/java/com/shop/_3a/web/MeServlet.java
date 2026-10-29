package com.shop._3a.web;

import com.shop._3a.model.User;
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
 * 个人中心：积分、等级、签到状态、积分流水与积分榜名次。
 */
@WebServlet("/me")
public class MeServlet extends BaseServlet {

    private final PointService pointService = new PointService();
    private final PostService postService = new PostService();
    private final RankService rankService = new RankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            redirect(resp, req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("levelTitle", pointService.levelTitle(user.getPoints()));
        req.setAttribute("nextLevelGap", pointService.nextLevelGap(user.getPoints()));
        req.setAttribute("signedToday", pointService.hasSignedInToday(user.getId()));
        req.setAttribute("records", pointService.recordsOf(user.getId()));

        List<RankService.RankItem> rank = rankService.pointsRank(0);
        int myRank = 0;
        for (RankService.RankItem item : rank) {
            if (item.getUser().getId() == user.getId()) {
                myRank = item.getRank();
                break;
            }
        }
        req.setAttribute("myRank", myRank);
        req.setAttribute("totalUsers", rank.size());
        req.setAttribute("postCount", postService.count());
        forward(req, resp, "/WEB-INF/views/me.jsp");
    }
}
