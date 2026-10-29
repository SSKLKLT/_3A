<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
%>
<div id="pet" class="pet" data-ctx="<%=ctx%>" title="拖动我 · 点击聊天 · 双击暂停乱逛">
    <div class="pet-bubble" id="petBubble">咕噜咕噜～</div>
    <img class="pet-img" id="petImg" src="<%=ctx%>/assets/pet.png" width="240" height="257"
         alt="大肥鱼娘" draggable="false">
    <div class="pet-name">大肥鱼娘</div>
</div>
<script src="<%=ctx%>/assets/pet.js"></script>
