package ai;

/**
 * Pluggable interface for AI-based test failure analysis.
 * <p>
 * Implementations receive the evidence captured when a scenario fails
 * (screenshot, page source, whatever error message was caught) and return
 * a best-effort diagnosis. Implementations must never throw — on any
 * failure (missing API key, network error, model unreachable, etc.) they
 * should return {@link Verdict#unavailable(String)} instead, so a missing
 * or misconfigured AI backend never breaks the actual test run.
 * <p>
 * Swap implementations via the AI_PROVIDER environment variable
 * (see {@link AIProviderFactory}) without touching any step definitions.
 */
public interface AIProvider {

    Verdict analyze(FailureContext context);
}
