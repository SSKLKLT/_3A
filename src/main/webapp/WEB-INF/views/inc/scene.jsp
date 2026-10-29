<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
%>
<div class="scene" aria-hidden="true">
    <div class="scene-art"></div>
    <div class="scene-caustic"></div>
    <div class="scene-caustic scene-caustic-2"></div>
    <div class="scene-rays"></div>
    <canvas class="scene-bubbles" id="sceneBubbles"></canvas>
    <div class="scene-veil"></div>
</div>
<script src="<%=ctx%>/assets/scene.js" defer></script>
