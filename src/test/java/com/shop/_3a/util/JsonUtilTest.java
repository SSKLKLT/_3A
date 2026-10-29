package com.shop._3a.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void escapeShouldHandleQuotesAndNewLines() {
        assertEquals("a\\nb", JsonUtil.escape("a\nb"));
        assertEquals("\\\"hi\\\"", JsonUtil.escape("\"hi\""));
        assertEquals("back\\\\slash", JsonUtil.escape("back\\slash"));
    }

    @Test
    void extractContentShouldReadChoicesMessage() throws IOException {
        String json = "{\"id\":\"1\",\"choices\":[{\"message\":{\"role\":\"assistant\","
                + "\"content\":\"第一行\\n第二行\\\"引用\\\"\"}}],\"usage\":{\"total_tokens\":10}}";

        assertEquals("第一行\n第二行\"引用\"", JsonUtil.extractContent(json));
    }

    @Test
    void extractContentShouldFailOnErrorResponse() {
        String json = "{\"error\":{\"message\":\"Invalid API key\",\"code\":401}}";

        IOException error = assertThrows(IOException.class, () -> JsonUtil.extractContent(json));
        assertTrue(error.getMessage().contains("Invalid API key"));
    }

    @Test
    void extractContentShouldFailOnEmptyBody() {
        assertThrows(IOException.class, () -> JsonUtil.extractContent(""));
        assertThrows(IOException.class, () -> JsonUtil.extractContent(null));
    }
}
