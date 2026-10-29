package com.shop._3a.web;

import com.shop._3a.model.User;
import com.shop._3a.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 注册：成功后赠送新人积分并自动登录。
 */
@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, "/WEB-INF/views/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = param(req, "username", "");
        String nickname = param(req, "nickname", "");
        String password = param(req, "password", "");
        req.setAttribute("username", username);
        req.setAttribute("nickname", nickname);
        try {
            User user = userService.register(username, password, nickname);
            HttpSession session = req.getSession(true);
            session.setAttribute(AuthFilter.USER_KEY, user);
            session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            redirect(resp, req.getContextPath() + "/me?welcome=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            forward(req, resp, "/WEB-INF/views/register.jsp");
        }
    }
}
