<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.util.SecurityUtil" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "注册 · 3A 小游戏社区");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="card narrow">
    <h2>注册新账号</h2>
    <p class="muted">注册即送 <%=com.shop._3a.service.PointService.BONUS_REGISTER%> 积分</p>
    <form method="post" action="<%=ctx%>/register" class="form">
        <label>用户名（3-16 位字母/数字/下划线）
            <input type="text" name="username" required
                   value="<%=SecurityUtil.escapeHtml(String.valueOf(request.getAttribute("username") == null ? "" : request.getAttribute("username")))%>">
        </label>
        <label>昵称（可留空）
            <input type="text" name="nickname"
                   value="<%=SecurityUtil.escapeHtml(String.valueOf(request.getAttribute("nickname") == null ? "" : request.getAttribute("nickname")))%>">
        </label>
        <label>密码（6-32 位）
            <input type="password" name="password" required>
        </label>
        <button class="btn btn-primary btn-block" type="submit">注册并登录</button>
    </form>
    <p class="muted">已有账号？<a href="<%=ctx%>/login">去登录</a></p>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
