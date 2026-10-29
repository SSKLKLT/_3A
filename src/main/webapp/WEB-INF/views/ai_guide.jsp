<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shop._3a.service.AiConfig, com.shop._3a.service.AiGuideService,
        com.shop._3a.util.FormatUtil, com.shop._3a.util.SecurityUtil, java.util.List" %>
<%
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "AI 攻略 · 3A 小游戏社区");
    AiGuideService.AiGuide guide = (AiGuideService.AiGuide) request.getAttribute("guide");
    List<String> hotGames = (List<String>) request.getAttribute("hotGames") == null
            ? List.of() : (List<String>) request.getAttribute("hotGames");
    String game = String.valueOf(request.getAttribute("game") == null ? "" : request.getAttribute("game"));
    String focus = String.valueOf(request.getAttribute("focus") == null ? "" : request.getAttribute("focus"));
    boolean configured = AiConfig.load().isConfigured();
%>
<jsp:include page="/WEB-INF/views/inc/header.jsp"/>
<div class="layout">
    <section class="main-col">
        <div class="card">
            <div class="post-head">
                <h3>AI 攻略助手</h3>
                <% if (configured) { %>
                <span class="badge badge-ok">已接入 <%=AiConfig.load().getModel()%></span>
                <% } else { %>
                <span class="badge badge-beta">占位模式</span>
                <% } %>
            </div>
            <p class="muted">输入游戏与关注点，大模型会直接生成中文攻略；未配置密钥时自动展示占位内容。</p>
            <form method="post" action="<%=ctx%>/ai-guide" class="form" id="guideForm"
                  onsubmit="return showThinking(this);">
                <label>游戏名称
                    <input type="text" name="game" required placeholder="如：艾尔登法环" value="<%=SecurityUtil.escapeHtml(game)%>">
                </label>
                <label>关注点（关卡 / Boss / 玩法，可留空）
                    <input type="text" name="focus" placeholder="如：女武神 Boss 打法" value="<%=SecurityUtil.escapeHtml(focus)%>">
                </label>
                <div class="form-row">
                    <button class="btn btn-primary" type="submit" id="guideBtn">生成攻略</button>
                    <span class="muted" id="thinking" style="display:none">🎏 大肥鱼娘正在翻攻略书…请稍候</span>
                </div>
            </form>
            <% if (!hotGames.isEmpty()) { %>
            <div class="hot-games">
                <span class="muted">热门：</span>
                <% for (String g : hotGames) { %>
                <button class="chip chip-btn" type="button"
                        onclick="var i=document.querySelector('input[name=game]');i.value='<%=SecurityUtil.escapeHtml(g)%>';i.focus();"><%=SecurityUtil.escapeHtml(g)%></button>
                <% } %>
            </div>
            <% } %>
        </div>

        <% if (guide != null) { %>
        <div class="card">
            <div class="post-head">
                <h3>《<%=SecurityUtil.escapeHtml(guide.getGame())%>》· <%=SecurityUtil.escapeHtml(guide.getFocus())%></h3>
                <% if (guide.isPlaceholder()) { %>
                <span class="badge badge-beta">占位内容</span>
                <% } else { %>
                <span class="badge badge-ok">AI 已生成</span>
                <% } %>
            </div>
            <p class="muted">生成时间：<%=FormatUtil.dateTime(guide.getGeneratedAt())%></p>
            <% if (guide.isPlaceholder()) { %>
            <div class="alert alert-info">占位攻略：<%=(guide.getNote() == null || guide.getNote().isEmpty()) ? "等待接入大模型" : SecurityUtil.escapeHtml(guide.getNote())%>。结果结构已就绪，接入后自动填充。</div>
            <% } %>
            <p class="guide-summary"><strong><%=SecurityUtil.escapeHtml(guide.getSummary())%></strong></p>
            <ol class="guide-list">
                <% for (String section : guide.getSections()) { %>
                <% if (section.startsWith("【")) { %>
                <li class="guide-title"><%=SecurityUtil.escapeHtml(section)%></li>
                <% } else { %>
                <li><%=SecurityUtil.escapeHtml(section)%></li>
                <% } %>
                <% } %>
            </ol>
            <% if (!guide.getRaw().isEmpty()) { %>
            <details class="raw-block">
                <summary>查看模型原始输出</summary>
                <pre><%=SecurityUtil.escapeHtml(guide.getRaw())%></pre>
            </details>
            <% } %>
        </div>
        <% } else { %>
        <div class="card empty">
            <p>输入游戏名称即可生成攻略骨架，例如「艾尔登法环」+「女武神」。</p>
            <p class="muted">生成结果会缓存 30 条，重复提问不会重复消耗额度。</p>
        </div>
        <% } %>
    </section>

    <aside class="side-col">
        <div class="card">
            <h3>接入状态</h3>
            <ul class="stat-list">
                <li><span>接口地址</span><strong class="small"><%=AiConfig.load().getBaseUrl()%></strong></li>
                <li><span>模型</span><strong class="small"><%=AiConfig.load().getModel()%></strong></li>
                <li><span>超时</span><strong><%=AiConfig.load().getTimeoutSeconds()%> 秒</strong></li>
                <li><span>结果缓存</span><strong>30 条</strong></li>
                <li><span>失败回退</span><strong>占位攻略</strong></li>
            </ul>
        </div>
        <div class="card">
            <h3>提示词策略</h3>
            <p class="muted">系统提示词要求模型只输出中文正文，用「【小节名】」分段，长度控制在 400 字以内，便于直接渲染成攻略卡片。</p>
        </div>
    </aside>
</div>
<script>
    function showThinking(form) {
        var game = form.querySelector('input[name=game]');
        if (!game || !game.value.trim()) {
            return false;
        }
        document.getElementById('guideBtn').disabled = true;
        document.getElementById('guideBtn').textContent = '生成中…';
        document.getElementById('thinking').style.display = 'inline';
        return true;
    }
</script>
<jsp:include page="/WEB-INF/views/inc/footer.jsp"/>
