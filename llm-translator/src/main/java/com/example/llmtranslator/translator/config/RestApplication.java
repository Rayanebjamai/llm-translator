package com.example.llmtranslator.translator.config;

// Imports the Jakarta REST Application class.
// This class is used to activate REST in the application.
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// Defines the base URL for all REST endpoints in this app.
// If we put "api", then every endpoint starts with /api.
@ApplicationPath("/api")
public class RestApplication extends Application {
    // This class can stay empty.
    // Its job is simply to tell WildFly:
    // "This application contains Jakarta REST resources,
    // and they all live under the /api base path."
}