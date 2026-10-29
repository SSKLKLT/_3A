<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.Comment, com.shop._3a.model.Post, com.shop._3a.model.User,
        com.shop._3a.util.FormatUtil, com.shop._3a.util.SecurityUtil, java.util.List" %>
<%
    String ctx = request.getContextPath();
    Post post = (Post) request.getAttribute("post");
    request.setAttribute("pageTitle", post.getTitle() + " · 3A 小游戏社区");
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");
    User me = (User) session.getAttribute("currentUser");
    String errorMsg = request.getParameter("error");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<% if (errorMsg != null) { %>
<div class="alert alert-error"><%=SecurityUtil.escapeHtml(errorMsg)%></div>
<% } %>
<% if ("1".equals(request.getParameter("created"))) { %>
<div class="alert alert-success">发帖成功，已获得 <%=com.shop._3a.service.PointService.BONUS_POST%> 积分</div>
<% } %>
<article class="card">
    <div class="post-head">
        <span class="badge badge-cat"><%=SecurityUtil.escapeHtml(post.getCategory())%></span>
        <% if (post.isPinned()) { %><span class="badge badge-pin">置顶</span><% } %>
        <h2><%=SecurityUtil.escapeHtml(post.getTitle())%></h2>
    </div>
    <div class="post-meta">
        <span><%=SecurityUtil.escapeHtml(post.getAuthorName())%></span>
        <span><%=FormatUtil.dateTime(post.getCreatedAt())%></span>
        <span><%=post.getViewCount()%> 浏览</span>
        <span><%=comments.size()%> 评论</span>
    </div>
    <div class="post-content"><%=SecurityUtil.escapeHtml(post.getContent()).replace("\n", "<br>")%></div>
    <% if (me != null && (me.isAdmin() || me.getId() == post.getAuthorId())) { %>
    <form method="post" action="<%=ctx%>/admin" class="inline-form">
        <input type="hidden" name="action" value="deletePost">
        <input type="hidden" name="postId" value="<%=post.getId()%>">
        <button class="btn btn-danger" type="submit">删除帖子</button>
    </form>
    <% } %>
</article>

<section class="card">
    <h3>评论区（<%=comments.size()%>）</h3>
    <% if (comments.isEmpty()) { %>
    <p class="muted">还没有评论，来说两句吧。</p>
    <% } else { %>
    <ul class="comment-list">
        <% for (Comment c : comments) { %>
        <li>
            <div class="comment-head">
                <strong><%=SecurityUtil.escapeHtml(c.getAuthorName())%></strong>
                <span class="muted"><%=FormatUtil.dateTime(c.getCreatedAt())%></span>
                <% if (me != null && (me.isAdmin() || me.getId() == c.getAuthorId())) { %>
                <form method="post" action="<%=ctx%>/admin" class="inline-form">
                    <input type="hidden" name="action" value="deleteComment">
                    <input type="hidden" name="postId" value="<%=post.getId()%>">
                    <input type="hidden" name="commentId" value="<%=c.getId()%>">
                    <button class="link-danger" type="submit">删除</button>
                </form>
                <% } %>
            </div>
            <p><%=SecurityUtil.escapeHtml(c.getContent())%></p>
        </li>
        <% } %>
    </ul>
    <% } %>

    <% if (me == null) { %>
    <p class="muted">请 <a href="<%=ctx%>/login?redirect=<%=ctx%>/posts/detail?id=<%=post.getId()%>">登录</a> 后参与评论。</p>
    <% } else { %>
    <form method="post" action="<%=ctx%>/comment" class="form">
        <input type="hidden" name="postId" value="<%=post.getId()%>">
        <label>发表评论（+<%=com.shop._3a.service.PointService.BONUS_COMMENT%> 积分）
            <textarea name="content" rows="3" required placeholder="友善交流，禁止引战…"></textarea>
        </label>
        <button class="btn btn-primary" type="submit">提交评论</button>
    </form>
    <% } %>
</section>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
