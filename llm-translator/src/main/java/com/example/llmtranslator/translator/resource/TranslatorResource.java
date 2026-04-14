package com.example.llmtranslator.translator.resource;



import com.example.llmtranslator.translator.dto.TranslationResponse;
import com.example.llmtranslator.translator.service.GeminiTranslationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// Defines the URL path for this resource.
// Combined with @ApplicationPath("/api"), the full base becomes /api/translate.
@Path("/translate")

// Says that this endpoint returns JSON.
@Produces(MediaType.APPLICATION_JSON)
public class TranslatorResource {

    // Injects the service bean managed by CDI/WildFly.
    @Inject
    private GeminiTranslationService translationService;

    // Handles HTTP GET requests.
    @GET
    public Response translate(@QueryParam("text") String text) {
        try {
            // Validate that the query parameter is present.
            if (text == null || text.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Query parameter 'text' is required.\"}")
                        .build();
            }

            // Call the service to perform the translation.
            String translated = translationService.translateToDarija(text);

            // Build a structured response object.
            TranslationResponse response = new TranslationResponse(
                    text,
                    translated,
                    translationService.getModelName()
            );

            // Return HTTP 200 with JSON body.
            return Response.ok(response).build();

        } catch (Exception e) {
            // Return HTTP 500 if something unexpected goes wrong.
            // In a bigger project, you would use a proper exception mapper.
            String safeMessage = e.getMessage() == null ? "Unexpected server error." : e.getMessage();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + safeMessage.replace("\"", "\\\"") + "\"}")
                    .build();
        }
    }
}