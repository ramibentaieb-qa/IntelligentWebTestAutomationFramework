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
 * Default AIProvider — calls Google's Gemini API (free tier available).
 * <p>
 * Configuration (environment variables):
 * <ul>
 *   <li>GEMINI_API_KEY (required) — get one free at https://aistudio.google.com</li>
 *   <li>GEMINI_MODEL (optional) — defaults to "gemini-2.5-flash". Check
 *       https://ai.google.dev/gemini-api/docs/models for current model names,
 *       since Google renames/retires models periodically.</li>
 * </ul>
 * If GEMINI_API_KEY is not set, {@link #analyze} returns
 * {@link Verdict#unavailable(String)} instead of failing the build — so a
 * fresh clone with no key configured still runs its tests normally, just
 * without AI analysis.
 */
public class GeminiApiProvider extends AbstractAIProvider {

    // Note: Google periodically restricts older model names to accounts that
    // already used them, returning 404 for fresh API keys. If this default
    // ever 404s for you, check https://ai.google.dev/gemini-api/docs/models
    // for the current recommended Flash model and override it with the
    // GEMINI_MODEL env var rather than waiting on a code change.
    private static final String DEFAULT_MODEL = "gemini-3.8-flash";
    private static final String ENDPOINT_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    private static final int PAGE_SOURCE_CHAR_LIMIT = 4000;
    private static final int MAX_ATTEMPTS = 3;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public Verdict analyze(FailureContext context) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return Verdict.unavailable("GEMINI_API_KEY is not set");
        }
        String model = System.getenv().getOrDefault("GEMINI_MODEL", DEFAULT_MODEL);

        try {
            JSONObject requestBody = buildRequestBody(context, model);
            String url = String.format(ENDPOINT_TEMPLATE, model, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = null;
            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                response = http.send(request, HttpResponse.BodyHandlers.ofString());
                if (!isTransient(response.statusCode()) || attempt == MAX_ATTEMPTS) {
                    break;
                }
                // Temporary Google-side problem (overloaded / rate limited): wait, then retry.
                long waitSeconds = 2L * attempt; // 2s, then 4s
                System.err.println("[AI] Gemini returned HTTP " + response.statusCode()
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

    /** Errors that usually clear up on their own, so they are worth retrying. */
    private boolean isTransient(int status) {
        return status == 429 || status == 500 || status == 502 || status == 503 || status == 504;
    }

    /** A specific message per status, so the developer debugs the right thing. */
    private String describeError(int status) {
        return switch (status) {
            case 400 -> "Gemini API returned HTTP 400 -- the request was rejected; check GEMINI_MODEL supports image input";
            case 401, 403 -> "Gemini API returned HTTP " + status + " -- check that GEMINI_API_KEY is valid";
            case 404 -> "Gemini API returned HTTP 404 -- the model was not found; set GEMINI_MODEL to a current model name";
            case 429 -> "Gemini API returned HTTP 429 -- free-tier rate limit reached; wait a minute and retry";
            case 500, 502, 503, 504 -> "Gemini API returned HTTP " + status
                    + " -- Google's service is temporarily unavailable (retried " + MAX_ATTEMPTS + " times); not a problem with your setup";
            default -> "Gemini API returned unexpected HTTP " + status;
        };
    }

    private JSONObject buildRequestBody(FailureContext context, String model) {
        String prompt = buildPrompt(context, PAGE_SOURCE_CHAR_LIMIT);

        JSONArray parts = new JSONArray();
        parts.put(new JSONObject().put("text", prompt));

        if (context.screenshotPng() != null) {
            JSONObject inlineData = new JSONObject()
                    .put("mime_type", "image/png")
                    .put("data", Base64.getEncoder().encodeToString(context.screenshotPng()));
            parts.put(new JSONObject().put("inline_data", inlineData));
        }

        JSONObject contentItem = new JSONObject()
                .put("role", "user")
                .put("parts", parts);

        return new JSONObject().put("contents", new JSONArray().put(contentItem));
    }

    private String extractText(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        return json.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");
    }
}