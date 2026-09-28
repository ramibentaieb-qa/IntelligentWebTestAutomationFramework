package ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

/**
 * Fully local, private alternative to {@link GeminiApiProvider}. Talks to a
 * locally running Ollama instance (https://ollama.com) — no data ever
 * leaves the machine. Select it by setting AI_PROVIDER=ollama.
 * <p>
 * Configuration (environment variables):
 * <ul>
 *   <li>OLLAMA_BASE_URL (optional) — defaults to http://localhost:11434</li>
 *   <li>OLLAMA_MODEL (optional) — defaults to "gemma3:4b". Pick a
 *       vision-capable model that fits your hardware, e.g.:
 *       {@code ollama pull gemma3:4b} for modest/CPU-only machines, or
 *       {@code ollama pull qwen3.8:27b} if you have a real GPU.</li>
 * </ul>
 * Requires {@code ollama serve} to be running and the chosen model already
 * pulled. Local inference on CPU-only hardware can take well over a minute
 * per failure — that's expected, not a bug.
 */
public class OllamaLocalProvider extends AbstractAIProvider {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "gemma3:4b";
    private static final int PAGE_SOURCE_CHAR_LIMIT = 3000;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public Verdict analyze(FailureContext context) {
        String baseUrl = System.getenv().getOrDefault("OLLAMA_BASE_URL", DEFAULT_BASE_URL);
        String model = System.getenv().getOrDefault("OLLAMA_MODEL", DEFAULT_MODEL);

        try {
            JSONObject requestBody = buildRequestBody(context, model);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/chat"))
                    // Local inference on modest hardware can be slow -- generous timeout.
                    .timeout(Duration.ofMinutes(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return Verdict.unavailable("Ollama returned HTTP " + response.statusCode()
                        + " -- is '" + model + "' pulled? (ollama pull " + model + ")");
            }

            String rawText = new JSONObject(response.body())
                    .getJSONObject("message")
                    .getString("content");

            return parseVerdict(rawText);

        } catch (ConnectException e) {
            return Verdict.unavailable("Could not reach Ollama at " + baseUrl
                    + " -- start it first with `ollama serve`");
        } catch (Exception e) {
            return Verdict.unavailable(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private JSONObject buildRequestBody(FailureContext context, String model) {
        String prompt = buildPrompt(context, PAGE_SOURCE_CHAR_LIMIT);

        JSONObject message = new JSONObject().put("role", "user").put("content", prompt);
        if (context.screenshotPng() != null) {
            message.put("images", new JSONArray()
                    .put(Base64.getEncoder().encodeToString(context.screenshotPng())));
        }

        return new JSONObject()
                .put("model", model)
                .put("stream", false)
                .put("messages", new JSONArray().put(message));
    }
}
