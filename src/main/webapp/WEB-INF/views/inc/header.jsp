<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.User" %>
<%
    String ctx = request.getContextPath();
    User me = (User) session.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? '3A 小游戏社区' : pageTitle}</title>
    <link rel="stylesheet" href="<%=ctx%>/assets/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/scene.jsp"/>
<header class="topbar">
    <div class="wrap topbar-inner">
        <a class="logo" href="<%=ctx%>/posts"><span class="logo-badge">3A</span> 小游戏社区</a>
        <nav class="nav">
            <a href="<%=ctx%>/posts">社区</a>
            <a href="<%=ctx%>/posts/new">发帖</a>
            <a href="<%=ctx%>/rank">排行榜</a>
            <a href="<%=ctx%>/ai-guide">AI 攻略</a>
            <% if (me != null) { %>
            <a href="<%=ctx%>/me">个人中心</a>
            <% } %>
            <% if (me != null && me.isAdmin()) { %>
            <a href="<%=ctx%>/admin" class="nav-admin">后台</a>
            <% } %>
        </nav>
        <div class="user-area">
            <% if (me == null) { %>
            <a class="btn btn-ghost" href="<%=ctx%>/login">登录</a>
            <a class="btn btn-primary" href="<%=ctx%>/register">注册</a>
            <% } else { %>
            <span class="chip"><%=me.getAvatar()%> <%=me.getNickname()%></span>
            <span class="chip chip-point"><%=me.getPoints()%> 积分</span>
            <a class="btn btn-ghost" href="<%=ctx%>/logout">退出</a>
            <% } %>
        </div>
    </div>
</header>
<main class="wrap">
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><%=request.getAttribute("error")%></div>
    <% } %>
    <% if (request.getAttribute("tip") != null || request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><%=request.getAttribute("success") != null ? request.getAttribute("success") : request.getAttribute("tip")%></div>
    <% } %>
