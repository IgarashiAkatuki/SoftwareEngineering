package com.bxtz.utils;

import com.bxtz.entity.Prompt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AIUtils {

    private String url = "http://localhost:11434/api/generate";

    public String getResponse(Prompt prompt){
        ObjectMapper mapper = new ObjectMapper();

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(prompt)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return extractContentFromJson(resp.body());
//            return resp.body();
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public String extractContentFromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.path("response").asText();
    }
}
