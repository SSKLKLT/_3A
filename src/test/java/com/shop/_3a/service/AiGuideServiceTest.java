package com.shop._3a.service;

import com.shop._3a.service.AiGuideService.AiGuide;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiGuideServiceTest {

    private static final String MARKDOWN = String.join("\n",
            "《艾尔登法环》女武神攻略速览",
            "【开荒准备】先把等级练到 120 级，武器强化 +9 以上。",
            "【核心机制】翻滚无敌帧要卡准，双持跳劈收益最高。",
            "- 推荐带火焰伤害武器",
            "1. 一阶段贴身输出",
            "【Boss 要点】水鸟乱舞侧向翻滚，二阶段等她落地再打。");

    /** 固定返回指定内容的桩客户端 */
    private static class StubClient extends LlmClient {
        private final String response;
        private final IOException error;

        StubClient(String response, IOException error) {
            super(AiConfig.of("http://localhost/v1", "test-key", "stub-model"));
            this.response = response;
            this.error = error;
        }

        @Override
        public String chat(String systemPrompt, String userPrompt) throws IOException {
            if (error != null) {
                throw error;
            }
            return response;
        }
    }

    @Test
    void generateShouldParseModelOutput() {
        AiGuideService service = new AiGuideService(new StubClient(MARKDOWN, null));

        AiGuide guide = service.generate("艾尔登法环", "女武神");

        assertEquals(AiGuideService.STATUS_GENERATED, guide.getStatus());
        assertFalse(guide.isPlaceholder());
        assertEquals("艾尔登法环", guide.getGame());
        assertEquals("女武神", guide.getFocus());
        assertEquals(6, guide.getSections().size());
        assertTrue(guide.getSections().stream().anyMatch(s -> s.startsWith("【开荒准备】")));
        assertTrue(guide.getRaw().contains("女武神"));
    }

    @Test
    void generateShouldCacheResult() {
        AiGuideService service = new AiGuideService(new StubClient(MARKDOWN, null));

        AiGuide first = service.generate("艾尔登法环", "女武神");
        AiGuide second = service.generate("艾尔登法环", "女武神");

        assertSame(first, second);
    }

    @Test
    void generateShouldFallBackWhenApiFails() {
        AiGuideService service = new AiGuideService(
                new StubClient(null, new IOException("HTTP 401：密钥无效")));

        AiGuide guide = service.generate("艾尔登法环", "");

        assertEquals(AiGuideService.STATUS_PLACEHOLDER, guide.getStatus());
        assertTrue(guide.isPlaceholder());
        assertTrue(guide.getNote().contains("HTTP 401"));
        assertEquals("全流程", guide.getFocus());
        assertEquals(7, guide.getSections().size());
    }

    @Test
    void generateShouldFallBackWhenNotConfigured() {
        AiGuideService service = new AiGuideService(
                new LlmClient(AiConfig.of("http://localhost/v1", "", "stub-model")));

        AiGuide guide = service.generate("", "");

        assertEquals("未命名作品", guide.getGame());
        assertEquals(AiGuideService.STATUS_PLACEHOLDER, guide.getStatus());
        assertTrue(guide.getNote().contains("未配置"));
    }

    @Test
    void toSectionsShouldStripMarkdown() {
        List<String> sections = AiGuideService.toSections(MARKDOWN);

        assertEquals(6, sections.size());
        assertEquals("《艾尔登法环》女武神攻略速览", sections.get(0));
        assertTrue(sections.contains("推荐带火焰伤害武器"));
        assertEquals("一阶段贴身输出", sections.get(4));
        assertFalse(sections.stream().anyMatch(s -> s.contains("**")));
    }

    @Test
    void firstLineShouldBeTruncated() {
        String longText = "很长的开头".repeat(40);

        assertEquals("很长的开头", AiGuideService.firstLine("很长的开头\n第二行"));
        assertTrue(AiGuideService.firstLine(longText).endsWith("…"));
    }
}
