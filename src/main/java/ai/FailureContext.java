package ai;

/**
 * Evidence captured at the moment a Cucumber scenario fails, bundled up
 * for an {@link AIProvider} to analyze.
 *
 * @param scenarioName human-readable scenario name, from Cucumber's Scenario
 * @param screenshotPng PNG bytes of the browser at the moment of failure, or null if unavailable
 * @param pageSource    the DOM/HTML at the moment of failure, or null if unavailable
 * @param errorMessage  the assertion/exception message that caused the failure, or null if not captured
 */
public record FailureContext(
        String scenarioName,
        byte[] screenshotPng,
        String pageSource,
        String errorMessage
) {
}
