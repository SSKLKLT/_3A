package com.shop._3a.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 攻略服务。
 * <p>
 * 已接入真实大模型（兼容 OpenAI 协议的 chat/completions 接口，默认 SiliconFlow）。
 * 未配置密钥或调用失败时，自动回退到占位攻略，保证页面始终可用。
 */
public class AiGuideService {

    /** 占位：未接入大模型或调用失败 */
    public static final String STATUS_PLACEHOLDER = "PLACEHOLDER";
    /** 已由大模型生成 */
    public static final String STATUS_GENERATED = "GENERATED";

    private static final String SYSTEM_PROMPT =
            "你是资深 3A 游戏攻略编辑。输出中文，结构清晰，只输出攻略正文，不要寒暄。"
                    + "使用 Markdown：每行一个要点，小节标题用「【小节名】」开头，"
                    + "例如「【开荒准备】推荐先把等级练到 30 级……」。总长度控制在 400 字以内。";

    private static final String[] DEFAULT_SECTIONS = {
            "【开荒准备】推荐等级、装备与消耗品清单（占位）",
            "【核心机制】本作战斗 / 资源循环要点解读（占位）",
            "【主线流程】关键节点顺序与支线取舍（占位）",
            "【Boss 要点】招式拆解与站位建议（占位）",
            "【收集要素】隐藏道具与成就路线（占位）"
    };

    private final LlmClient client;
    private final Map<String, AiGuide> cache;

    public AiGuideService() {
        this(new LlmClient());
    }

    public AiGuideService(LlmClient client) {
        this.client = client;
        this.cache = Collections.synchronizedMap(new LinkedHashMap<String, AiGuide>(32, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, AiGuide> eldest) {
                return size() > 30;
            }
        });
    }

    /**
     * 生成攻略：命中缓存直接返回，否则调用大模型。
     */
    public AiGuide generate(String game, String focus) {
        String gameName = (game == null || game.isBlank()) ? "未命名作品" : game.trim();
        String topic = (focus == null || focus.isBlank()) ? "全流程" : focus.trim();

        String key = gameName + "||" + topic;
        AiGuide cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        AiGuide guide;
        if (client == null || !client.isConfigured()) {
            guide = placeholder(gameName, topic, "未配置大模型密钥，当前展示占位攻略");
        } else {
            try {
                String markdown = client.chat(SYSTEM_PROMPT, buildPrompt(gameName, topic));
                List<String> sections = toSections(markdown);
                if (sections.isEmpty()) {
                    guide = placeholder(gameName, topic, "大模型未返回有效内容");
                } else {
                    guide = new AiGuide(gameName, topic, firstLine(markdown), sections,
                            STATUS_GENERATED, LocalDateTime.now(), markdown, "");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                guide = placeholder(gameName, topic, "调用大模型被中断");
            } catch (Exception e) {
                guide = placeholder(gameName, topic, "调用大模型失败：" + e.getMessage());
            }
        }
        if (STATUS_GENERATED.equals(guide.getStatus())) {
            cache.put(key, guide);
        }
        return guide;
    }

    private String buildPrompt(String gameName, String topic) {
        return "请为游戏《" + gameName + "》生成「" + topic + "」攻略，包含开荒准备、核心机制、"
                + "关键流程、Boss 要点与收集要素五个小节。";
    }

    private AiGuide placeholder(String gameName, String topic, String note) {
        List<String> sections = new ArrayList<>();
        sections.add("目标作品：" + gameName + " ｜ 关注点：" + topic);
        Collections.addAll(sections, DEFAULT_SECTIONS);
        sections.add("提示：配置密钥后将由大模型生成真实攻略，本占位内容会被替换。");
        String summary = "《" + gameName + "》·" + topic + " 攻略骨架已生成，共 "
                + DEFAULT_SECTIONS.length + " 个章节待填充。";
        return new AiGuide(gameName, topic, summary, sections, STATUS_PLACEHOLDER, LocalDateTime.now(), "", note);
    }

    /** 把 Markdown 输出整理成章节列表 */
    static List<String> toSections(String markdown) {
        List<String> sections = new ArrayList<>();
        if (markdown == null) {
            return sections;
        }
        for (String rawLine : markdown.split("\\R")) {
            String line = rawLine.trim()
                    .replaceAll("^\\s*>\\s?", "")
                    .replaceAll("^#+\\s*", "")
                    .replaceAll("^[-*+]\\s*", "")
                    .replaceAll("^\\d+[.、)]\\s*", "")
                    .replace("**", "")
                    .trim();
            if (line.isEmpty() || "```".equals(line)) {
                continue;
            }
            sections.add(line);
        }
        return sections;
    }

    static String firstLine(String markdown) {
        List<String> sections = toSections(markdown);
        if (sections.isEmpty()) {
            return "";
        }
        String first = sections.get(0);
        return first.length() <= 120 ? first : first.substring(0, 120) + "…";
    }

    /** AI 攻略结果 */
    public static class AiGuide {
        private final String game;
        private final String focus;
        private final String summary;
        private final List<String> sections;
        private final String status;
        private final LocalDateTime generatedAt;
        private final String raw;
        private final String note;

        public AiGuide(String game, String focus, String summary, List<String> sections,
                       String status, LocalDateTime generatedAt, String raw, String note) {
            this.game = game;
            this.focus = focus;
            this.summary = summary;
            this.sections = sections;
            this.status = status;
            this.generatedAt = generatedAt;
            this.raw = raw;
            this.note = note;
        }

        public String getGame() {
            return game;
        }

        public String getFocus() {
            return focus;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getSections() {
            return sections;
        }

        public String getStatus() {
            return status;
        }

        public boolean isPlaceholder() {
            return STATUS_PLACEHOLDER.equals(status);
        }

        public LocalDateTime getGeneratedAt() {
            return generatedAt;
        }

        public String getRaw() {
            return raw;
        }

        public String getNote() {
            return note;
        }
    }
}
