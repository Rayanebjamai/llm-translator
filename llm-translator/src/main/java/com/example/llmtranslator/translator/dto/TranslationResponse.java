package com.example.llmtranslator.translator.dto;

// A simple DTO (Data Transfer Object).
// This class represents the JSON response returned to the client.
public class TranslationResponse {

    // The original input text sent by the client.
    private String originalText;

    // The translated text returned by Gemini.
    private String translatedText;

    // The name of the LLM model used.
    private String model;

    // Empty constructor required by many frameworks for serialization/deserialization.
    public TranslationResponse() {
    }

    // Constructor used when we already know all field values.
    public TranslationResponse(String originalText, String translatedText, String model) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.model = model;
    }

    // Getter for originalText.
    public String getOriginalText() {
        return originalText;
    }

    // Setter for originalText.
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    // Getter for translatedText.
    public String getTranslatedText() {
        return translatedText;
    }

    // Setter for translatedText.
    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    // Getter for model.
    public String getModel() {
        return model;
    }

    // Setter for model.
    public void setModel(String model) {
        this.model = model;
    }
}
