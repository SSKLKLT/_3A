package com.shop._3a.service;

import com.shop._3a.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 大模型客户端：调用兼容 OpenAI 协议的 chat/completions 接口（SiliconFlow）。
 */
public class LlmClient {

    private final AiConfig config;

    public LlmClient() {
        this(AiConfig.load());
    }

    public LlmClient(AiConfig config) {
        this.config = config == null ? AiConfig.load() : config;
    }

    public boolean isConfigured() {
        return config.isConfigured();
    }

    public AiConfig getConfig() {
        return config;
    }

    /**
     * 发起对话，返回模型输出的文本。
     *
     * @throws IOException          网络/接口异常
     * @throws InterruptedException 线程被中断
     */
    public String chat(String systemPrompt, String userPrompt) throws IOException, InterruptedException {
        return chat(systemPrompt, userPrompt, config.getTimeoutSeconds());
    }

    /** 指定超时的对话调用（桌宠闲聊等交互场景用更短的超时） */
    public String chat(String systemPrompt, String userPrompt, int timeoutSeconds)
            throws IOException, InterruptedException {
        String body = "{"
                + "\"model\":\"" + JsonUtil.escape(config.getModel()) + "\","
                + "\"stream\":false,"
                + "\"temperature\":0.8,"
                + "\"max_tokens\":1200,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"" + JsonUtil.escape(systemPrompt) + "\"},"
                + "{\"role\":\"user\",\"content\":\"" + JsonUtil.escape(userPrompt) + "\"}"
                + "]}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint()))
                .timeout(Duration.ofSeconds(Math.max(5, timeoutSeconds)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP " + response.statusCode() + "：" + clip(response.body()));
        }
        return JsonUtil.extractContent(response.body());
    }

    private String endpoint() {
        String base = config.getBaseUrl().endsWith("/")
                ? config.getBaseUrl().substring(0, config.getBaseUrl().length() - 1)
                : config.getBaseUrl();
        return base + "/chat/completions";
    }

    private String clip(String text) {
        if (text == null) {
            return "";
        }
        String flat = text.replaceAll("\\s+", " ").trim();
        return flat.length() <= 200 ? flat : flat.substring(0, 200) + "…";
    }
}
