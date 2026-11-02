
package stepdefinitions;

import ai.AIProvider;
import ai.AIProviderFactory;
import ai.FailureContext;
import ai.Verdict;
import base.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    private final TestContext context;

    private final AIProvider aiProvider = AIProviderFactory.create();

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        context.initDriver();
    }

    private void analyzeFailure(Scenario scenario) {

        byte[] screenshot = null;
        String pageSource = null;

        try {
            screenshot = ((TakesScreenshot) context.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            scenario.attach(screenshot, "image/png", "failure-screenshot");

        } catch (Exception e) {
            System.out.println("[AI] Could not capture screenshot: " + e.getMessage());
        }

        try {
            pageSource = context.getDriver().getPageSource();

        } catch (Exception e) {
            System.out.println("[AI] Could not capture page source: " + e.getMessage());
        }

        String errorMessage = context.getErrorMessage();

        FailureContext failureContext = new FailureContext(
                scenario.getName(),
                screenshot,
                pageSource,
                errorMessage
        );

        Verdict verdict = aiProvider.analyze(failureContext);

        String report = "[AI FAILURE ANALYSIS] "
                + verdict.category() + " -- " + verdict.summary();

        if (verdict.suggestedFix() != null
                && !verdict.suggestedFix().isBlank()) {
            report += " | Suggested fix: " + verdict.suggestedFix();
        }

        System.out.println(report);

        scenario.attach(report, "text/plain", "ai-analysis");

        appendToMarkdownReport(scenario, verdict);
    }

    private void appendToMarkdownReport(Scenario scenario, Verdict verdict) {

        java.io.File reportFile =
                new java.io.File("target/ai-analysis-report.md");

        reportFile.getParentFile().mkdirs();

        boolean isNewFile = !reportFile.exists();

        try (java.io.FileWriter writer =
                     new java.io.FileWriter(reportFile, true)) {

            if (isNewFile) {
                writer.write("# AI Failure Analysis Report\n\n");
            }

            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss"));

            writer.write("## " + scenario.getName() + "\n\n");
            writer.write("- **Time:** " + timestamp + "\n");
            writer.write("- **Category:** " + verdict.category() + "\n");
            writer.write("- **Summary:** " + verdict.summary() + "\n");

            if (verdict.suggestedFix() != null
                    && !verdict.suggestedFix().isBlank()) {
                writer.write("- **Suggested fix:** "
                        + verdict.suggestedFix() + "\n");
            }

            writer.write("\n---\n\n");

        } catch (java.io.IOException e) {
            System.err.println("[AI] Could not write report: " + e.getMessage());
        }
    }

    @After
    public void tearDown(Scenario scenario) {

        try {
            if (scenario.isFailed() && context.getDriver() != null) {
                analyzeFailure(scenario);
            }
        } catch (Exception e) {
            System.err.println("[AI] Failure analysis error: " + e.getMessage());
        } finally {
            context.quitDriver();
        }
    }
}
