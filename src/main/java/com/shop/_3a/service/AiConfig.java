package com.shop._3a.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 大模型配置。
 * <p>
 * 读取优先级（后者覆盖前者）：
 * <ol>
 *   <li>classpath 下的 {@code ai-config.properties}</li>
 *   <li>外部配置文件：{@code ${catalina.base}/conf/ai-config.properties}、{@code ~/.3a-ai-config.properties}</li>
 *   <li>环境变量 / JVM 系统属性</li>
 * </ol>
 * 生产环境请把密钥放到外部文件或环境变量，不要打进 war 包。
 */
public class AiConfig {

    public static final String DEFAULT_BASE_URL = "https://api.siliconflow.cn/v1";
    public static final String DEFAULT_MODEL = "deepseek-ai/DeepSeek-V4-Flash";

    private String baseUrl = DEFAULT_BASE_URL;
    private String apiKey = "";
    private String model = DEFAULT_MODEL;
    private int timeoutSeconds = 60;

    public static AiConfig load() {
        Properties props = new Properties();

        try (InputStream in = AiConfig.class.getResourceAsStream("/ai-config.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException ignored) {
            // 忽略，走默认值
        }

        loadExternal(props, externalPath("catalina.base"));
        loadExternal(props, externalPath("user.home") + File.separator + ".3a-ai-config.properties");

        override(props, "ai.base.url", "AI_BASE_URL");
        override(props, "ai.api.key", "AI_API_KEY");
        override(props, "ai.model", "AI_MODEL");
        override(props, "ai.timeout.seconds", "AI_TIMEOUT_SECONDS");

        AiConfig config = new AiConfig();
        config.baseUrl = trim(props.getProperty("ai.base.url"), DEFAULT_BASE_URL);
        config.apiKey = trim(props.getProperty("ai.api.key"), "");
        config.model = trim(props.getProperty("ai.model"), DEFAULT_MODEL);
        config.timeoutSeconds = parseInt(props.getProperty("ai.timeout.seconds"), 60);
        return config;
    }

    /** 便于测试：直接构造配置 */
    public static AiConfig of(String baseUrl, String apiKey, String model) {
        AiConfig config = new AiConfig();
        config.baseUrl = baseUrl == null || baseUrl.isBlank() ? DEFAULT_BASE_URL : baseUrl;
        config.apiKey = apiKey == null ? "" : apiKey;
        config.model = model == null || model.isBlank() ? DEFAULT_MODEL : model;
        return config;
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    private static void loadExternal(Properties props, String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (!file.isFile()) {
            return;
        }
        try (InputStream in = new FileInputStream(file)) {
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // 忽略，继续用已加载的配置
        }
    }

    private static String externalPath(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value + File.separator + "conf" + File.separator + "ai-config.properties";
    }

    private static void override(Properties props, String key, String envName) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        if (value != null && !value.isBlank()) {
            props.setProperty(key, value.trim());
        }
    }

    private static String trim(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
