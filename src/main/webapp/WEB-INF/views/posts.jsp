<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.model.Comment, com.shop._3a.model.Post, com.shop._3a.model.PostCard,
        com.shop._3a.model.User, com.shop._3a.service.RankService, com.shop._3a.service.PointService,
        com.shop._3a.util.FormatUtil, com.shop._3a.util.SecurityUtil, java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "社区 · 3A 小游戏");
    List<PostCard> cards = (List<PostCard>) request.getAttribute("cards");
    String keyword = String.valueOf(request.getAttribute("keyword") == null ? "" : request.getAttribute("keyword"));
    String category = String.valueOf(request.getAttribute("category") == null ? "" : request.getAttribute("category"));
    String sort = String.valueOf(request.getAttribute("sort") == null ? "new" : request.getAttribute("sort"));
    int pageNo = (Integer) request.getAttribute("page");
    int totalPages = (Integer) request.getAttribute("totalPages");
    int total = (Integer) request.getAttribute("total");
    Map<String, Integer> overview = (Map<String, Integer>) request.getAttribute("overview");
    List<RankService.RankItem> topUsers = (List<RankService.RankItem>) request.getAttribute("topUsers");
    List<Comment> recentComments = (List<Comment>) request.getAttribute("recentComments");
    User me = (User) session.getAttribute("currentUser");
    boolean signedToday = Boolean.TRUE.equals(request.getAttribute("signedToday"));
    String kwEnc = java.net.URLEncoder.encode(keyword, "UTF-8");
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>

<section class="hero">
    <div class="hero-main">
        <h1>3A 小游戏社区</h1>
        <p class="hero-slogan">和同好一起开荒、拆招、写攻略——从《艾尔登法环》到《黑神话：悟空》，这里只聊 3A。</p>
        <div class="hero-actions">
            <a class="btn btn-primary" href="<%=ctx%>/posts/new">✍️ 发布帖子</a>
            <a class="btn" href="<%=ctx%>/rank">🏆 看看排行</a>
            <a class="btn" href="<%=ctx%>/ai-guide">🎏 问 AI 攻略</a>
        </div>
        <div class="hero-stats">
            <% for (Map.Entry<String, Integer> e : overview.entrySet()) { %>
            <div class="hero-stat">
                <strong><%=e.getValue()%></strong>
                <span><%=e.getKey()%></span>
            </div>
            <% } %>
        </div>
    </div>
    <div class="hero-girl">
        <div class="girl-glow"></div>
        <img class="hero-figure" src="<%=ctx%>/assets/hero-girl.png" width="318" height="340"
             alt="大肥鱼娘 3D 形象" draggable="false">
    </div>
</section>

<div class="notice-bar">
    <span>📢 大肥鱼娘已上线，右下角点它就能聊天</span>
    <span class="muted">积分规则：发帖 +<%=PointService.BONUS_POST%> · 评论 +<%=PointService.BONUS_COMMENT%> · 签到 +<%=PointService.BONUS_SIGN_IN%></span>
</div>

<div class="layout">
    <section class="main-col">
        <div class="card">
            <form class="search-bar" method="get" action="<%=ctx%>/posts">
                <input type="text" name="keyword" placeholder="搜索标题 / 内容 / 作者" value="<%=SecurityUtil.escapeHtml(keyword)%>">
                <input type="hidden" name="sort" value="<%=SecurityUtil.escapeHtml(sort)%>">
                <input type="hidden" name="category" value="<%=SecurityUtil.escapeHtml(category)%>">
                <button class="btn btn-primary" type="submit">搜索</button>
                <a class="btn btn-ghost" href="<%=ctx%>/posts/new">+ 发帖</a>
            </form>
            <div class="tabs">
                <a class="tab <%=category.isEmpty() ? "active" : ""%>" href="<%=ctx%>/posts?sort=<%=sort%>">全部</a>
                <% for (String c : Post.CATEGORIES) { %>
                <a class="tab <%=category.equals(c) ? "active" : ""%>"
                   href="<%=ctx%>/posts?category=<%=c%>&sort=<%=sort%>&keyword=<%=kwEnc%>"><%=c%></a>
                <% } %>
                <span class="spacer"></span>
                <a class="tab <%= "new".equals(sort) ? "active" : ""%>"
                   href="<%=ctx%>/posts?category=<%=category%>&sort=new&keyword=<%=kwEnc%>">最新</a>
                <a class="tab <%= "hot".equals(sort) ? "active" : ""%>"
                   href="<%=ctx%>/posts?category=<%=category%>&sort=hot&keyword=<%=kwEnc%>">热门</a>
            </div>
        </div>

        <% if (cards == null || cards.isEmpty()) { %>
        <div class="card empty">没有找到相关帖子，换个关键词试试，或者来发第一篇吧～</div>
        <% } else { %>
        <% for (PostCard card : cards) { %>
        <article class="card post-item">
            <div class="post-avatar"><%=card.getAuthorAvatar()%></div>
            <div class="post-body">
                <div class="post-head">
                    <span class="badge badge-cat"><%=SecurityUtil.escapeHtml(card.getPost().getCategory())%></span>
                    <% if (card.getPost().isPinned()) { %><span class="badge badge-pin">置顶</span><% } %>
                    <% if (card.isHot()) { %><span class="badge badge-hot">🔥 热帖</span><% } %>
                    <h3><a href="<%=ctx%>/posts/detail?id=<%=card.getPost().getId()%>"><%=SecurityUtil.escapeHtml(card.getPost().getTitle())%></a></h3>
                </div>
                <p class="post-summary"><%=SecurityUtil.escapeHtml(card.getPost().getSummary())%></p>
                <div class="post-meta">
                    <span><%=SecurityUtil.escapeHtml(card.getPost().getAuthorName())%></span>
                    <span>🕒 <%=FormatUtil.dateTime(card.getPost().getCreatedAt())%></span>
                    <span>👁 <%=card.getPost().getViewCount()%> 浏览</span>
                    <span>💬 <%=card.getCommentCount()%> 评论</span>
                </div>
            </div>
        </article>
        <% } %>
        <div class="pager">
            <% if (pageNo > 1) { %>
            <a class="btn btn-ghost" href="<%=ctx%>/posts?page=<%=pageNo - 1%>&category=<%=category%>&sort=<%=sort%>&keyword=<%=kwEnc%>">上一页</a>
            <% } %>
            <span class="muted">第 <%=pageNo%> / <%=totalPages%> 页，共 <%=total%> 篇</span>
            <% if (pageNo < totalPages) { %>
            <a class="btn btn-ghost" href="<%=ctx%>/posts?page=<%=pageNo + 1%>&category=<%=category%>&sort=<%=sort%>&keyword=<%=kwEnc%>">下一页</a>
            <% } %>
        </div>
        <% } %>
    </section>

    <aside class="side-col">
        <% if (me != null) { %>
        <div class="card">
            <h3>每日签到</h3>
            <p class="muted">每天可领 <%=PointService.BONUS_SIGN_IN%> 积分，连续签到能更快冲榜。</p>
            <% if (signedToday) { %>
            <button class="btn btn-block" disabled>今日已签到 ✓</button>
            <% } else { %>
            <form method="post" action="<%=ctx%>/sign-in">
                <button class="btn btn-primary btn-block" type="submit">签到领积分</button>
            </form>
            <% } %>
            <a class="link-more" href="<%=ctx%>/me">我的积分与流水 →</a>
        </div>
        <% } else { %>
        <div class="card">
            <h3>加入社区</h3>
            <p class="muted">注册即送 <%=PointService.BONUS_REGISTER%> 积分，立刻发帖、评论、冲榜。</p>
            <a class="btn btn-primary btn-block" href="<%=ctx%>/register">注册账号</a>
            <a class="link-more" href="<%=ctx%>/login">已有账号，去登录 →</a>
        </div>
        <% } %>

        <div class="card">
            <h3>积分榜 TOP 5</h3>
            <ol class="rank-mini">
                <% for (RankService.RankItem item : topUsers) { %>
                <li>
                    <span class="rank-no rank-<%=item.getRank()%>"><%=item.getRank()%></span>
                    <span><%=item.getUser().getAvatar()%> <%=SecurityUtil.escapeHtml(item.getUser().getNickname())%></span>
                    <strong><%=item.getScore()%></strong>
                </li>
                <% } %>
            </ol>
            <a class="link-more" href="<%=ctx%>/rank">查看完整榜单 →</a>
        </div>

        <div class="card">
            <h3>最新评论</h3>
            <ul class="side-comments">
                <% if (recentComments.isEmpty()) { %>
                <li class="muted">还没有评论。</li>
                <% } else { %>
                <% for (Comment c : recentComments) { %>
                <li>
                    <p><%=SecurityUtil.escapeHtml(c.getContent().length() > 28 ? c.getContent().substring(0, 28) + "…" : c.getContent())%></p>
                    <span class="muted">— <%=SecurityUtil.escapeHtml(c.getAuthorName())%> ·
                        <a href="<%=ctx%>/posts/detail?id=<%=c.getPostId()%>">查看原帖</a></span>
                </li>
                <% } %>
                <% } %>
            </ul>
        </div>

        <div class="card">
            <h3>积分规则</h3>
            <ul class="stat-list">
                <li><span>新人注册</span><strong>+<%=PointService.BONUS_REGISTER%></strong></li>
                <li><span>发布帖子</span><strong>+<%=PointService.BONUS_POST%></strong></li>
                <li><span>发表评论</span><strong>+<%=PointService.BONUS_COMMENT%></strong></li>
                <li><span>每日签到</span><strong>+<%=PointService.BONUS_SIGN_IN%></strong></li>
            </ul>
        </div>

        <div class="card pet-tip-card">
            <h3>桌宠 · 大肥鱼娘 🎏</h3>
            <img class="pet-thumb" src="<%=ctx%>/assets/pet.png" width="240" height="257" alt="大肥鱼娘">
            <p class="muted">蓝发白衣、鱼尾摆摆的社区看板娘。在右下角陪你逛社区：拖动换位、点击聊天、双击让它别乱跑。</p>
            <p class="muted">它的回答来自 <%=com.shop._3a.service.AiConfig.load().getModel()%>。</p>
        </div>
    </aside>
</div>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
