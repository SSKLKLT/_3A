<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.util.SecurityUtil" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "提示 · 3A 小游戏社区");
    String message = String.valueOf(request.getAttribute("message") == null ? "页面无法访问" : request.getAttribute("message"));
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="card narrow center">
    <h2><%=SecurityUtil.escapeHtml(message)%></h2>
    <p class="muted">请确认链接是否正确，或返回社区首页。</p>
    <a class="btn btn-primary" href="<%=ctx%>/posts">返回社区</a>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
