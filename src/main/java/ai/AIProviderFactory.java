package ai;

/**
 * Chooses which AIProvider implementation to use, based on the AI_PROVIDER
 * environment variable:
 * <ul>
 *   <li>"groq" (default) — {@link GroqApiProvider}, needs GROQ_API_KEY</li>
 *   <li>"none" — {@link NoOpProvider}, AI analysis disabled</li>
 * </ul>
 * This is the single place step definitions need to know about — swapping
 * backends never requires touching step definition code.
 */
public final class AIProviderFactory {

    private AIProviderFactory() {
    }

    public static AIProvider create() {
        String provider = System.getenv().getOrDefault("AI_PROVIDER", "groq").trim().toLowerCase();
        return switch (provider) {
            case "none", "off", "disabled" -> new NoOpProvider();
            default -> new GroqApiProvider();
        };
    }
}