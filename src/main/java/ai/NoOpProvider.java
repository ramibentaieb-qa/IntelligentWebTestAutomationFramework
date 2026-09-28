package ai;

/**
 * Disables AI analysis entirely. Selected with AI_PROVIDER=none — useful for
 * running the suite fast with zero network calls (e.g. quick local iteration).
 */
public class NoOpProvider implements AIProvider {

    @Override
    public Verdict analyze(FailureContext context) {
        return Verdict.unavailable("AI_PROVIDER is set to 'none'");
    }
}
