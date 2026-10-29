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
 * 登录：GET 展示表单，POST 校验并写入会话。
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(resp, req.getContextPath() + "/posts");
            return;
        }
        req.setAttribute("redirect", param(req, "redirect", ""));
        forward(req, resp, "/WEB-INF/views/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = param(req, "username", "");
        String password = param(req, "password", "");
        User user = userService.login(username, password);
        if (user == null) {
            req.setAttribute("error", "用户名或密码错误");
            req.setAttribute("username", username);
            forward(req, resp, "/WEB-INF/views/login.jsp");
            return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute(AuthFilter.USER_KEY, user);
        session.setMaxInactiveInterval(7 * 24 * 60 * 60);

        String target = param(req, "redirect", "");
        if (target.isEmpty()) {
            target = req.getContextPath() + "/posts";
        }
        redirect(resp, target);
    }
}
