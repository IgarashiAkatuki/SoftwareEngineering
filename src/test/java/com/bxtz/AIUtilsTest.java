package com.bxtz.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AIUtilsTest {

    private final AIUtils aiUtils = new AIUtils();

    @Test
    void testExtractContentFromJson_validJson() throws Exception {
        String jsonResponse = "{\"model\":\"qwen2.5:0.5b\",\"created_at\":\"2025-05-23T15:56:15.566Z\",\"response\":\"This is the AI response.\",\"done\":true}";
        String expectedContent = "This is the AI response.";
        String actualContent = aiUtils.extractContentFromJson(jsonResponse); //
        assertEquals(expectedContent, actualContent, "Extracted content should match 'response' field");
    }

    @Test
    void testExtractContentFromJson_responseFieldMissing() {
        String jsonResponse = "{\"model\":\"qwen2.5:0.5b\",\"created_at\":\"2025-05-23T15:56:15.566Z\",\"done\":true}";
        // The current implementation of extractContentFromJson will return an empty string if "response" is missing.
        String expectedContent = "";
        assertDoesNotThrow(() -> {
            String actualContent = aiUtils.extractContentFromJson(jsonResponse);
            assertEquals(expectedContent, actualContent, "Should return empty string if 'response' field is missing");
        });
    }

    @Test
    void testExtractContentFromJson_invalidJson() {
        String invalidJson = "this is not json";
        assertThrows(Exception.class, () -> {
            aiUtils.extractContentFromJson(invalidJson);
        }, "Should throw an exception for invalid JSON input");
    }
}