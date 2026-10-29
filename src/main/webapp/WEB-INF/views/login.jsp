<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.util.SecurityUtil" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "登录 · 3A 小游戏社区");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="card narrow">
    <h2>登录社区</h2>
    <p class="muted">演示账号：admin / admin123（管理员），zelda_fan / 123456（普通用户）</p>
    <form method="post" action="<%=ctx%>/login" class="form">
        <input type="hidden" name="redirect" value="<%=SecurityUtil.escapeHtml(String.valueOf(request.getAttribute("redirect") == null ? "" : request.getAttribute("redirect")))%>">
        <label>用户名
            <input type="text" name="username" required
                   value="<%=SecurityUtil.escapeHtml(String.valueOf(request.getAttribute("username") == null ? "" : request.getAttribute("username")))%>">
        </label>
        <label>密码
            <input type="password" name="password" required>
        </label>
        <button class="btn btn-primary btn-block" type="submit">登录</button>
    </form>
    <p class="muted">还没有账号？<a href="<%=ctx%>/register">立即注册</a></p>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
