package ai;

import org.json.JSONObject;

/**
 * Shared prompt-building and response-parsing logic for AIProvider
 * implementations. Not part of the public contract — providers can ignore
 * this and build their own prompts if a given backend needs something
 * different (e.g. a two-model pipeline).
 */
abstract class AbstractAIProvider implements AIProvider {

    protected String buildPrompt(FailureContext context, int pageSourceCharLimit) {
        return """
                You are analyzing a failed Selenium/Cucumber UI test.
                Scenario: %s
                Assertion/error message from the test: %s

                A screenshot of the browser at the moment of failure may be attached.
                Below is the page's HTML source at that same moment (truncated).

                Classify the failure into exactly ONE of these categories. Read the
                definitions carefully -- they are easy to confuse:

                - LOCATOR_BROKEN: a selector (id/name/xpath/css/tag) could not find an
                  element that SHOULD exist on a normally functioning page -- e.g. the
                  app rendered correctly but the test's selector is stale or wrong.

                - ASSERTION_MISMATCH: the application behaved differently than the test
                  expected, for reasons unrelated to locators or infrastructure. This
                  INCLUDES cases where the test supplied wrong/invalid input (e.g. a
                  bad password) and the app correctly rejected it, causing a downstream
                  element (like a dashboard header) to never appear -- that is a data/
                  expectation mismatch, not a locator or environment problem. Also use
                  this when an error message's exact text changed.

                - TIMING_FLAKE: the evidence suggests a race condition (element likely
                  would have appeared after a short delay) rather than a real defect.

                - ENVIRONMENT_ISSUE: use ONLY for infrastructure problems unrelated to
                  app logic or test data -- e.g. the browser/driver crashed, the target
                  server was unreachable, a network timeout occurred, or the page never
                  loaded at all.

                - UNKNOWN: none of the above can be determined confidently from the
                  evidence given.

                If the error message or page content shows the app intentionally
                rejected the test's input (e.g. "Invalid credentials"), that is almost
                always ASSERTION_MISMATCH, never ENVIRONMENT_ISSUE.

                Respond with ONLY a compact JSON object, no markdown code fences, no extra text,
                in exactly this shape:
                {"category": "...", "summary": "one or two sentences", "suggestedFix": "one or two sentences"}

                Page source (truncated to %d chars):
                %s
                """.formatted(
                context.scenarioName(),
                (context.errorMessage() == null || context.errorMessage().isBlank())
                        ? "not captured" : context.errorMessage(),
                pageSourceCharLimit,
                truncate(context.pageSource(), pageSourceCharLimit)
        );
    }

    /**
     * Parses a model's raw text reply into a Verdict. Tolerant of markdown
     * code fences and of models that don't follow the JSON instruction
     * perfectly — falls back to surfacing the raw text rather than failing.
     */
    protected Verdict parseVerdict(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return new Verdict(Verdict.Category.UNKNOWN, "Model returned an empty response.", "");
        }
        try {
            String cleaned = rawText.trim()
                    .replaceAll("(?i)^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
            JSONObject json = new JSONObject(cleaned);

            Verdict.Category category;
            try {
                category = Verdict.Category.valueOf(json.optString("category", "UNKNOWN").trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                category = Verdict.Category.UNKNOWN;
            }

            return new Verdict(
                    category,
                    json.optString("summary", rawText),
                    json.optString("suggestedFix", "")
            );
        } catch (Exception e) {
            // Model didn't return clean JSON -- still surface its raw answer rather than failing the pipeline.
            return new Verdict(Verdict.Category.UNKNOWN, rawText.trim(), "");
        }
    }

    protected String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + " ...(truncated)";
    }
}