package com.bxtz.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class MarkdownUtils {

    public String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    public WebView renderMarkdown(String markdown) {
        String html = markdownToHtml(markdown);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String htmlPage = """
    <html>
    <head>
      <style>
        body { font-family: 'Arial'; padding: 10px; }
        pre { background: #f0f0f0; padding: 5px; }
        code { font-family: monospace; }
      </style>
    </head>
    <body>%s</body>
    </html>
    """.formatted(html);

        webEngine.loadContent(htmlPage);
        return webView;
    }
}
