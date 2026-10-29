package com.shop._3a.util;

import java.io.IOException;

/**
 * 极简 JSON 工具：仅处理本项目需要的字符串转义与字段提取，避免引入三方依赖。
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /** 生成 JSON 字符串字面量（含转义） */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 从形如 {@code {"choices":[{"message":{"content":"..."}}]}} 的响应中提取 content。
     *
     * @throws IOException 找不到字段时抛出，便于上层回退到占位内容
     */
    public static String extractContent(String json) throws IOException {
        if (json == null || json.isBlank()) {
            throw new IOException("接口返回内容为空");
        }
        String error = extractError(json);
        int contentIndex = json.indexOf("\"content\"");
        if (contentIndex < 0) {
            throw new IOException("接口返回异常：" + (error != null ? error
                    : json.substring(0, Math.min(200, json.length()))));
        }
        int colon = json.indexOf(':', contentIndex + 9);
        if (colon < 0) {
            throw new IOException("接口返回格式无法解析");
        }
        int start = json.indexOf('"', colon + 1);
        if (start < 0) {
            throw new IOException("接口返回格式无法解析");
        }
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = start + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            sb.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                    }
                    default -> sb.append(c);
                }
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                return sb.toString();
            }
            sb.append(c);
        }
        throw new IOException("接口返回内容未正常结束");
    }

    /** 提取 {"error":{"message":"..."}} 中的错误描述 */
    private static String extractError(String json) {
        int index = json.indexOf("\"error\"");
        if (index < 0) {
            return null;
        }
        String tail = json.substring(index, Math.min(json.length(), index + 400));
        String message = tail.contains("\"message\"") ? extractByKey(tail, "\"message\"") : null;
        return message == null ? "未知错误" : message;
    }

    private static String extractByKey(String json, String key) {
        int index = json.indexOf(key);
        if (index < 0) {
            return null;
        }
        int start = json.indexOf('"', json.indexOf(':', index + key.length()));
        if (start < 0) {
            return null;
        }
        int end = json.indexOf('"', start + 1);
        return end < 0 ? null : json.substring(start + 1, end);
    }
}
