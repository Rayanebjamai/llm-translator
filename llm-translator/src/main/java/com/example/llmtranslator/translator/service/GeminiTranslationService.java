package com.example.llmtranslator.translator.service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class GeminiTranslationService implements TranslationService {

    // Gemini model name
    private static final String MODEL_NAME = "gemini-3-flash-preview";

    // Replace YOUR_API_KEY_HERE with your own key on your computer only
    private static final String API_KEY = "AIzaSyCUxdgxNi0U43VeGOWZ1qlDIIbdkUSTePk";

    // HTTP client used to call Gemini
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String translateToDarija(String text) throws Exception {

        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("Please replace YOUR_API_KEY_HERE with your real Gemini API key.");
        }

        String prompt = "Translate the following text to Moroccan Darija written in Latin characters, not Arabic script. "
                + "Return only the translation, with no explanation: " + text;

        JsonObject requestBody = Json.createObjectBuilder()
                .add("contents", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("parts", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder()
                                                .add("text", prompt)))))
                .build();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + MODEL_NAME
                + ":generateContent?key="
                + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Gemini API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            JsonObject jsonResponse = reader.readObject();

            JsonArray candidates = jsonResponse.getJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new IOException("No candidates found in Gemini response.");
            }

            JsonObject firstCandidate = candidates.getJsonObject(0);
            JsonObject content = firstCandidate.getJsonObject("content");
            if (content == null) {
                throw new IOException("No content found in Gemini response.");
            }

            JsonArray parts = content.getJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                throw new IOException("No parts found in Gemini response.");
            }

            JsonObject firstPart = parts.getJsonObject(0);
            String translatedText = firstPart.getString("text", null);

            if (translatedText == null || translatedText.isBlank()) {
                throw new IOException("Translated text is empty.");
            }

            return translatedText.trim();
        }
    }

    public String getModelName() {
        return MODEL_NAME;
    }
}