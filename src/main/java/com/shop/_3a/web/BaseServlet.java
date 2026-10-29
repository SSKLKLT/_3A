package com.shop._3a.web;

import com.shop._3a.model.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet 基类：封装登录用户读取、参数读取与跳转。
 */
public abstract class BaseServlet extends HttpServlet {

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(AuthFilter.USER_KEY);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }

    protected void redirect(HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(path);
    }

    protected String param(HttpServletRequest req, String name, String defaultValue) {
        String value = req.getParameter(name);
        return value == null ? defaultValue : value.trim();
    }

    protected long paramLong(HttpServletRequest req, String name, long defaultValue) {
        try {
            return Long.parseLong(param(req, name, ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected int paramInt(HttpServletRequest req, String name, int defaultValue) {
        try {
            return Integer.parseInt(param(req, name, ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
