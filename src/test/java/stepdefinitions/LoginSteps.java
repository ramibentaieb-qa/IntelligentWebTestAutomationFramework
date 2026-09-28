package stepdefinitions;

import ai.AIProvider;
import ai.AIProviderFactory;
import ai.FailureContext;
import ai.Verdict;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.BasePage;
import pages.DashboardPage;
import pages.LoginPage;

import java.time.Duration;


public class LoginSteps {

    protected WebDriver driver;
    protected BasePage basePage;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;

    // Captured when a step assertion fails, so tearDown can pass it to the AI provider.
    private String lastErrorMessage;

    // Picks Gemini / Ollama / none based on the AI_PROVIDER env var -- see AIProviderFactory.
    private final AIProvider aiProvider = AIProviderFactory.create();


    private String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";


    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--diasbla-dev-shm-usage");
        driver = new ChromeDriver(options);

//        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        basePage = new BasePage();
        basePage.setDriver(driver);
        loginPage = new LoginPage();
        lastErrorMessage = null;
    }
    @Given("the user is on the login page")
    public void navigateToLoginPage() {
        driver.get(url);
    }

    @When("he enters his {string} and his {string}")
    public void enterCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @And("clicks on the login button")
    public void submitLogin() {
        this.dashboardPage = loginPage.clickOnButtonLogin();
    }

    @Then("he should be redirected to the {string} page")
    public void verifyDashboardPage(String expectedResult) {
        try {
                if (expectedResult.contains("Dashboard")) {
                    String actualResult = dashboardPage.getDashboardTitle();
                    Assert.assertEquals(expectedResult, actualResult);
                } else {
                    String actualError = loginPage.getErrorMessage();
                    Assert.assertEquals(expectedResult, actualError);
                }
            } catch (AssertionError | RuntimeException e) {
                // Capture for the AI analyzer, then rethrow so Cucumber still fails the scenario normally.
                lastErrorMessage = e.getMessage();
                throw e;
            }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            analyzeFailure(scenario);
        }
        if (driver != null)  {
            driver.quit();
        }
    }

    private void analyzeFailure(Scenario scenario) {
        byte[] screenshot = null;
        String pageSource = null;

        try {
            screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        } catch (Exception e) {
            System.out.println("[AI] Could not capture screenshot: " + e.getMessage());
        }

        try {
            pageSource = driver.getPageSource();
        } catch (Exception e) {
            System.err.println("[AI] Could not capture page source: " + e.getMessage());
        }

        FailureContext context = new FailureContext(scenario.getName(), screenshot, pageSource, lastErrorMessage);
        Verdict verdict = aiProvider.analyze(context);

        String report = "[AI FAILURE ANALYSIS] " + verdict.category() + " -- " + verdict.summary()
                + ((verdict.suggestedFix() == null || verdict.suggestedFix().isBlank())
                ? "" : " | Suggested fix: " + verdict.suggestedFix());

        System.out.println(report);
        scenario.attach(report, "text/plain", "ai-analysis");
    }
}
