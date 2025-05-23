package com.bxtz.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarkdownUtilsTest {

    private final MarkdownUtils markdownUtils = new MarkdownUtils();

    @Test
    void testMarkdownToHtml_basic() {
        String markdown = "# Header 1\n\n**Bold Text**";
        String expectedHtml = "<h1>Header 1</h1>\n<p><strong>Bold Text</strong></p>\n"; // flexmark might produce slightly different spacing/newlines

        String actualHtml = markdownUtils.markdownToHtml(markdown); //

        // Normalize whitespace and compare core content for robustness
        assertEquals(
                expectedHtml.replaceAll("\\s+", "").toLowerCase(),
                actualHtml.replaceAll("\\s+", "").toLowerCase(),
                "HTML output should match expected basic markdown conversion"
        );
    }

    @Test
    void testMarkdownToHtml_emptyInput() {
        String markdown = "";
        String expectedHtml = ""; // Or "<p></p>\n" depending on parser leniency
        String actualHtml = markdownUtils.markdownToHtml(markdown);
        assertEquals(expectedHtml.trim(), actualHtml.trim(), "HTML output for empty markdown should be empty");
    }
}