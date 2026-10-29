<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.PointRecord, com.shop._3a.model.User, com.shop._3a.util.FormatUtil,
        com.shop._3a.util.SecurityUtil, java.util.List" %>
<%
    String ctx = request.getContextPath();
    User me = (User) session.getAttribute("currentUser");
    request.setAttribute("pageTitle", "个人中心 · 3A 小游戏社区");
    List<PointRecord> records = (List<PointRecord>) request.getAttribute("records");
    int myRank = (Integer) request.getAttribute("myRank");
    int totalUsers = (Integer) request.getAttribute("totalUsers");
    boolean signedToday = (Boolean) request.getAttribute("signedToday");
    int nextGap = (Integer) request.getAttribute("nextLevelGap");
    String sign = request.getParameter("sign");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<% if ("signed".equals(sign)) { %>
<div class="alert alert-success">签到成功，积分已到账</div>
<% } else if ("already".equals(sign)) { %>
<div class="alert alert-info">今天已经签到过啦，明天再来</div>
<% } %>
<% if ("1".equals(request.getParameter("welcome"))) { %>
<div class="alert alert-success">注册成功，已赠送 <%=com.shop._3a.service.PointService.BONUS_REGISTER%> 积分</div>
<% } %>
<div class="layout">
    <section class="main-col">
        <div class="card">
            <div class="profile">
                <div class="avatar-lg"><%=me.getAvatar()%></div>
                <div>
                    <h2><%=SecurityUtil.escapeHtml(me.getNickname())%></h2>
                    <p class="muted">@<%=SecurityUtil.escapeHtml(me.getUsername())%>
                        · <%=me.isAdmin() ? "管理员" : "普通玩家"%>
                        · 加入于 <%=FormatUtil.date(me.getCreatedAt().toLocalDate())%></p>
                    <p>
                        <span class="badge badge-level"><%=request.getAttribute("levelTitle")%></span>
                        <% if (nextGap > 0) { %><span class="muted">距下一等级还需 <%=nextGap%> 积分</span><% } %>
                    </p>
                </div>
            </div>
            <ul class="stat-list horizontal">
                <li><span>积分</span><strong><%=me.getPoints()%></strong></li>
                <li><span>积分榜名次</span><strong><%=myRank > 0 ? myRank + " / " + totalUsers : "未上榜"%></strong></li>
                <li><span>今日签到</span><strong><%=signedToday ? "已签到" : "未签到"%></strong></li>
            </ul>
            <% if (!signedToday) { %>
            <form method="post" action="<%=ctx%>/sign-in">
                <button class="btn btn-primary" type="submit">签到 +<%=com.shop._3a.service.PointService.BONUS_SIGN_IN%></button>
            </form>
            <% } %>
        </div>

        <div class="card">
            <h3>积分流水</h3>
            <% if (records.isEmpty()) { %>
            <p class="muted">暂无积分记录。</p>
            <% } else { %>
            <table class="table">
                <thead>
                <tr>
                    <th>时间</th>
                    <th>事由</th>
                    <th>变动</th>
                </tr>
                </thead>
                <tbody>
                <% for (PointRecord r : records) { %>
                <tr>
                    <td><%=FormatUtil.dateTime(r.getCreatedAt())%></td>
                    <td><%=SecurityUtil.escapeHtml(r.getReason())%></td>
                    <td class="<%=r.getDelta() >= 0 ? "plus" : "minus"%>"><%=r.getDelta() >= 0 ? "+" : ""%><%=r.getDelta()%></td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </section>

    <aside class="side-col">
        <div class="card">
            <h3>积分规则</h3>
            <ul class="stat-list">
                <li><span>新人注册</span><strong>+<%=com.shop._3a.service.PointService.BONUS_REGISTER%></strong></li>
                <li><span>发布帖子</span><strong>+<%=com.shop._3a.service.PointService.BONUS_POST%></strong></li>
                <li><span>发表评论</span><strong>+<%=com.shop._3a.service.PointService.BONUS_COMMENT%></strong></li>
                <li><span>每日签到</span><strong>+<%=com.shop._3a.service.PointService.BONUS_SIGN_IN%></strong></li>
            </ul>
        </div>
    </aside>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
