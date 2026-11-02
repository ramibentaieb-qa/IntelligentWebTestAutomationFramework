package stepdefinitions;

import ai.AIProvider;
import ai.AIProviderFactory;
import ai.FailureContext;
import ai.Verdict;
import base.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.BasePage;
import pages.DashboardPage;
import pages.LoginPage;

import java.time.Duration;

import static pages.BasePage.driver;


public class LoginSteps {

    private final TestContext context;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    // Captured when a step assertion fails, so tearDown can pass it to the AI provider.
    private String lastErrorMessage;

    // Picks Gemini / Ollama / none based on the AI_PROVIDER env var -- see AIProviderFactory.
    private final AIProvider aiProvider = AIProviderFactory.create();



    @Given("the user is on the login page")
    public void navigateToLoginPage() {
        context.getDriver().get(TestContext.url);
    }

    @When("he enters his {string} and his {string}")
    public void enterCredentials(String username, String password) {
        context.loginPage().enterUsername(username);
        context.loginPage().enterPassword(password);
    }

    @And("clicks on the login button")
    public void submitLogin() {
        context.loginPage().clickOnButtonLogin();
    }

    @Then("he should be redirected to the {string} page")
    public void verifyDashboardPage(String expectedResult) {
        try {
                if (expectedResult.contains("Dashboard")) {
                    String actualResult = context.dashboardPage().getDashboardTitle();
                    Assertions.assertEquals(expectedResult, actualResult);
                } else {
                    boolean actualError = context.loginPage().getErrorMessage();
                    Assertions.assertTrue(actualError, expectedResult);
                }
            } catch (AssertionError | RuntimeException e) {
                lastErrorMessage = e.getMessage();
                throw e;
            }
    }
}
