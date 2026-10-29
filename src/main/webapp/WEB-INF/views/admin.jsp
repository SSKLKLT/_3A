<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.Comment, com.shop._3a.model.Post, com.shop._3a.model.User,
        com.shop._3a.util.FormatUtil, com.shop._3a.util.SecurityUtil, java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "后台管理 · 3A 小游戏社区");
    List<Post> posts = (List<Post>) request.getAttribute("posts");
    List<User> users = (List<User>) request.getAttribute("users");
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");
    Map<String, Integer> overview = (Map<String, Integer>) request.getAttribute("overview");
    String tip = request.getParameter("tip");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<% if (tip != null && !tip.isEmpty()) { %>
<div class="alert alert-success"><%=SecurityUtil.escapeHtml(tip)%></div>
<% } %>
<div class="card">
    <h2>后台概览</h2>
    <ul class="stat-list horizontal">
        <% for (Map.Entry<String, Integer> e : overview.entrySet()) { %>
        <li><span><%=e.getKey()%></span><strong><%=e.getValue()%></strong></li>
        <% } %>
    </ul>
</div>

<div class="card">
    <h3>内容审核 · 帖子（<%=posts.size()%>）</h3>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>标题</th>
            <th>作者</th>
            <th>板块</th>
            <th>时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (Post p : posts) { %>
        <tr>
            <td><%=p.getId()%></td>
            <td><a href="<%=ctx%>/posts/detail?id=<%=p.getId()%>"><%=SecurityUtil.escapeHtml(p.getTitle())%></a></td>
            <td><%=SecurityUtil.escapeHtml(p.getAuthorName())%></td>
            <td><%=SecurityUtil.escapeHtml(p.getCategory())%></td>
            <td><%=FormatUtil.dateTime(p.getCreatedAt())%></td>
            <td>
                <form method="post" action="<%=ctx%>/admin" class="inline-form">
                    <input type="hidden" name="action" value="pinPost">
                    <input type="hidden" name="postId" value="<%=p.getId()%>">
                    <button class="link" type="submit"><%=p.isPinned() ? "取消置顶" : "置顶"%></button>
                </form>
                <form method="post" action="<%=ctx%>/admin" class="inline-form">
                    <input type="hidden" name="action" value="deletePost">
                    <input type="hidden" name="postId" value="<%=p.getId()%>">
                    <button class="link-danger" type="submit">删除</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="card">
    <h3>内容审核 · 评论（<%=comments.size()%>）</h3>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>所属帖子</th>
            <th>内容</th>
            <th>作者</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (Comment c : comments) { %>
        <tr>
            <td><%=c.getId()%></td>
            <td><a href="<%=ctx%>/posts/detail?id=<%=c.getPostId()%>">#<%=c.getPostId()%></a></td>
            <td><%=SecurityUtil.escapeHtml(c.getContent())%></td>
            <td><%=SecurityUtil.escapeHtml(c.getAuthorName())%></td>
            <td>
                <form method="post" action="<%=ctx%>/admin" class="inline-form">
                    <input type="hidden" name="action" value="deleteComment">
                    <input type="hidden" name="postId" value="<%=c.getPostId()%>">
                    <input type="hidden" name="commentId" value="<%=c.getId()%>">
                    <button class="link-danger" type="submit">删除</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="card">
    <h3>用户（<%=users.size()%>）</h3>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>昵称</th>
            <th>用户名</th>
            <th>角色</th>
            <th>积分</th>
            <th>注册时间</th>
        </tr>
        </thead>
        <tbody>
        <% for (User u : users) { %>
        <tr>
            <td><%=u.getId()%></td>
            <td><%=u.getAvatar()%> <%=SecurityUtil.escapeHtml(u.getNickname())%></td>
            <td>@<%=SecurityUtil.escapeHtml(u.getUsername())%></td>
            <td><%=u.isAdmin() ? "管理员" : "玩家"%></td>
            <td><%=u.getPoints()%></td>
            <td><%=FormatUtil.dateTime(u.getCreatedAt())%></td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
