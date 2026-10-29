<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.service.RankService, com.shop._3a.util.SecurityUtil, java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "排行榜 · 3A 小游戏社区");
    List<RankService.RankItem> items = (List<RankService.RankItem>) request.getAttribute("items");
    String tab = (String) request.getAttribute("tab");
    Map<String, Integer> overview = (Map<String, Integer>) request.getAttribute("overview");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="card">
    <h2>社区排行榜</h2>
    <div class="tabs">
        <a class="tab <%= "points".equals(tab) ? "active" : ""%>" href="<%=ctx%>/rank?tab=points">积分榜</a>
        <a class="tab <%= "active".equals(tab) ? "active" : ""%>" href="<%=ctx%>/rank?tab=active">7 日活跃榜</a>
        <span class="spacer"></span>
        <% for (Map.Entry<String, Integer> e : overview.entrySet()) { %>
        <span class="muted"><%=e.getKey()%> <%=e.getValue()%>　</span>
        <% } %>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th>名次</th>
            <th>玩家</th>
            <th>等级</th>
            <th><%= "active".equals(tab) ? "活跃度（发帖+评论）" : "积分"%></th>
        </tr>
        </thead>
        <tbody>
        <% if (items.isEmpty()) { %>
        <tr>
            <td colspan="4" class="muted">暂无数据</td>
        </tr>
        <% } else { %>
        <% for (RankService.RankItem item : items) { %>
        <tr class="<%=item.getRank() <= 3 ? "top-row" : ""%>">
            <td class="rank-cell">
                <span class="rank-no rank-<%=item.getRank()%>"><%=item.getRank()%></span>
            </td>
            <td><%=item.getUser().getAvatar()%> <%=SecurityUtil.escapeHtml(item.getUser().getNickname())%>
                <span class="muted">@<%=SecurityUtil.escapeHtml(item.getUser().getUsername())%></span>
            </td>
            <td><span class="badge badge-level"><%=item.getLevelTitle()%></span></td>
            <td><strong><%=item.getScore()%></strong></td>
        </tr>
        <% } %>
        <% } %>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
