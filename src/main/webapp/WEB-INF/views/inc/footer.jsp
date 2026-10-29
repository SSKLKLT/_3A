<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
</main>
<footer class="footer wrap">
    <p>3A 小游戏社区 · Servlet + JSP 演示项目（数据存于内存，重启后重置）</p>
    <p class="muted">发帖 +10 · 评论 +5 · 每日签到 +20 · AI 攻略由
        <%=com.shop._3a.service.AiConfig.load().getModel()%> 提供 · 桌宠：大肥鱼娘</p>
</footer>
<jsp:include page="/WEB-INF/views/inc/pet.jsp"/>
</body>
</html>
