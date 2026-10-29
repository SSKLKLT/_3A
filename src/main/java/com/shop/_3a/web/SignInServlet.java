package com.shop._3a.web;

import com.shop._3a.model.User;
import com.shop._3a.service.PointService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 每日签到，发放积分。
 */
@WebServlet("/sign-in")
public class SignInServlet extends BaseServlet {

    private final PointService pointService = new PointService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            redirect(resp, req.getContextPath() + "/login");
            return;
        }
        String result = pointService.signIn(user.getId()) ? "signed" : "already";
        redirect(resp, req.getContextPath() + "/me?sign=" + result);
    }
}
