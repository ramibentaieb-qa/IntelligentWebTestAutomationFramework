package ai;

/**
 * The result of AI-based failure analysis.
 *
 * @param category      best-guess root-cause category
 * @param summary       one or two sentence human-readable explanation
 * @param suggestedFix  one or two sentence suggested next step, may be blank
 */
public record Verdict(
        Category category,
        String summary,
        String suggestedFix
) {

    public enum Category {
        /** A locator (id/name/xpath/css) likely no longer matches the page. */
        LOCATOR_BROKEN,
        /** The app behaved differently than expected — a real functional/regression bug, or a copy change. */
        ASSERTION_MISMATCH,
        /** Looks like a timing/race condition rather than a real defect. */
        TIMING_FLAKE,
        /** Looks like an infrastructure/environment problem (network, driver, CI runner). */
        ENVIRONMENT_ISSUE,
        /** Could not be classified confidently, or the AI backend wasn't available. */
        UNKNOWN
    }

    /**
     * Used whenever the AI backend itself couldn't run (no API key, network
     * error, Ollama not running, etc.) — this is a normal, expected outcome
     * for a fresh clone with no AI backend configured, not an error state.
     */
    public static Verdict unavailable(String reason) {
        return new Verdict(Category.UNKNOWN, "AI analysis unavailable: " + reason, "");
    }
}
