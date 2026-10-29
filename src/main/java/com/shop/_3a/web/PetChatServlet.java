package com.shop._3a.web;

import com.shop._3a.service.AiConfig;
import com.shop._3a.service.LlmClient;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 桌宠闲聊接口：大肥鱼娘的人设对话，返回纯文本，失败时使用内置台词。
 */
@WebServlet("/api/pet-chat")
public class PetChatServlet extends BaseServlet {

    private static final String SYSTEM_PROMPT =
            "你现在是 3A 小游戏社区里的桌宠「大肥鱼娘」，一条圆滚滚、爱吐槽又靠谱的肥鱼精。"
                    + "说话可爱、简短（50 字以内），可以带一个 emoji，只用中文回答，不要自称 AI。";

    private static final List<String> FALLBACK_LINES = List.of(
            "咕噜咕噜～今天也要多刷几个 Boss 呀 🐟",
            "本鱼已经把攻略藏在肚子里了，去 AI 攻略页问我吧！",
            "签到领 20 积分，别忘记啦～",
            "我游得快，是因为我圆滚滚的身材有浮力 💙",
            "别摸了别摸了，鱼鳞都要被摸掉了～"
    );

    private final LlmClient client = new LlmClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(reply(param(req, "q", "")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String reply(String question) {
        if (question == null || question.isBlank()) {
            return randomLine();
        }
        try {
            String answer = client.chat(SYSTEM_PROMPT, question, 25);
            if (answer != null && !answer.isBlank()) {
                return answer.trim();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
            // 接口异常时回退到内置台词
        }
        return randomLine();
    }

    private String randomLine() {
        return FALLBACK_LINES.get((int) (System.currentTimeMillis() % FALLBACK_LINES.size()));
    }

    /** 供页面展示当前模型名 */
    public static String modelName() {
        return AiConfig.load().getModel();
    }
}
