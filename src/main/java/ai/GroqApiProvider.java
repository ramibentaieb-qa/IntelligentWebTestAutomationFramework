package ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

/**
 * Default AIProvider — calls Groq's API (free tier, higher rate limits than
 * Gemini's free tier).
 * <p>
 * Configuration (environment variables):
 * <ul>
 *   <li>GROQ_API_KEY (required) — get one free at https://console.groq.com/keys</li>
 *   <li>GROQ_MODEL (optional) — defaults to "qwen/qwen3.8-27b". Check
 *       https://console.groq.com/docs/vision for current vision-capable
 *       model names, since these change over time.</li>
 * </ul>
 * If GROQ_API_KEY is not set, {@link #analyze} returns
 * {@link Verdict#unavailable(String)} instead of failing the build — so a
 * fresh clone with no key configured still runs its tests normally, just
 * without AI analysis.
 */
public class GroqApiProvider extends AbstractAIProvider {

    private static final String DEFAULT_MODEL = "qwen/qwen3.8-27b";
    private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final int PAGE_SOURCE_CHAR_LIMIT = 4000;
    private static final int MAX_ATTEMPTS = 3;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public Verdict analyze(FailureContext context) {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return Verdict.unavailable("GROQ_API_KEY is not set");
        }
        String model = System.getenv().getOrDefault("GROQ_MODEL", DEFAULT_MODEL);

        try {
            JSONObject requestBody = buildRequestBody(context, model);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = null;
            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                response = http.send(request, HttpResponse.BodyHandlers.ofString());
                if (!isTransient(response.statusCode()) || attempt == MAX_ATTEMPTS) {
                    break;
                }
                long waitSeconds = 2L * attempt; // 2s, then 4s
                System.err.println("[AI] Groq returned HTTP " + response.statusCode()
                        + " (temporary), retrying in " + waitSeconds + "s (attempt "
                        + attempt + "/" + MAX_ATTEMPTS + ")");
                Thread.sleep(waitSeconds * 1000);
            }

            if (response.statusCode() != 200) {
                return Verdict.unavailable(describeError(response.statusCode()));
            }

            String rawText = extractText(response.body());
            return parseVerdict(rawText);

        } catch (Exception e) {
            return Verdict.unavailable(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private boolean isTransient(int status) {
        return status == 429 || status == 500 || status == 502 || status == 503 || status == 504;
    }

    private String describeError(int status) {
        return switch (status) {
            case 400 -> "Groq API returned HTTP 400 -- the request was rejected; check GROQ_MODEL supports image input";
            case 401, 403 -> "Groq API returned HTTP " + status + " -- check that GROQ_API_KEY is valid";
            case 404 -> "Groq API returned HTTP 404 -- the model was not found; set GROQ_MODEL to a current model name";
            case 429 -> "Groq API returned HTTP 429 -- rate limit reached; wait a moment and retry";
            case 500, 502, 503, 504 -> "Groq API returned HTTP " + status
                    + " -- Groq's service is temporarily unavailable (retried " + MAX_ATTEMPTS + " times); not a problem with your setup";
            default -> "Groq API returned unexpected HTTP " + status;
        };
    }

    private JSONObject buildRequestBody(FailureContext context, String model) {
        String prompt = buildPrompt(context, PAGE_SOURCE_CHAR_LIMIT);

        JSONArray contentParts = new JSONArray();
        contentParts.put(new JSONObject().put("type", "text").put("text", prompt));

        if (context.screenshotPng() != null) {
            String base64 = Base64.getEncoder().encodeToString(context.screenshotPng());
            JSONObject imageUrl = new JSONObject().put("url", "data:image/png;base64," + base64);
            contentParts.put(new JSONObject().put("type", "image_url").put("image_url", imageUrl));
        }

        JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", contentParts);

        return new JSONObject()
                .put("model", model)
                .put("messages", new JSONArray().put(message));
    }

    private String extractText(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}