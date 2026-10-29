<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.Post, com.shop._3a.util.SecurityUtil" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "发帖 · 3A 小游戏社区");
    String[] categories = (String[]) request.getAttribute("categories");
    if (categories == null) {
        categories = Post.CATEGORIES;
    }
    String title = String.valueOf(request.getAttribute("title") == null ? "" : request.getAttribute("title"));
    String content = String.valueOf(request.getAttribute("content") == null ? "" : request.getAttribute("content"));
    String category = String.valueOf(request.getAttribute("category") == null ? "讨论" : request.getAttribute("category"));
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="card narrow">
    <h2>发布新帖</h2>
    <p class="muted">发布成功可获得 <%=com.shop._3a.service.PointService.BONUS_POST%> 积分</p>
    <form method="post" action="<%=ctx%>/posts/new" class="form">
        <label>标题（4-60 字）
            <input type="text" name="title" required value="<%=SecurityUtil.escapeHtml(title)%>">
        </label>
        <label>板块
            <select name="category">
                <% for (String c : categories) { %>
                <option value="<%=c%>" <%=c.equals(category) ? "selected" : ""%>><%=c%></option>
                <% } %>
            </select>
        </label>
        <label>正文（5-5000 字）
            <textarea name="content" rows="10" required><%=SecurityUtil.escapeHtml(content)%></textarea>
        </label>
        <button class="btn btn-primary btn-block" type="submit">发布</button>
    </form>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
