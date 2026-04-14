package com.example.llmtranslator.translator.service;


// Small service interface.
// Having an interface is good design because it separates
// "what the service does" from "how it does it".
public interface TranslationService {

    // Translates a given text into Moroccan Darija.
    String translateToDarija(String text) throws Exception;
}