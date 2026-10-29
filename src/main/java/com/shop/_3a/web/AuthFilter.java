package com.shop._3a.web;

import com.shop._3a.model.User;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 登录校验：未登录访问受保护地址时跳转登录页。
 */
@WebFilter({"/posts/new", "/comment", "/me", "/sign-in", "/admin"})
public class AuthFilter implements Filter {

    public static final String USER_KEY = "currentUser";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(USER_KEY);
        if (user == null) {
            String from = req.getRequestURI();
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + from);
            return;
        }
        chain.doFilter(request, response);
    }
}
