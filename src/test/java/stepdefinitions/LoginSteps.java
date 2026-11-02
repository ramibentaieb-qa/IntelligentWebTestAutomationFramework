package stepdefinitions;

import base.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;




public class LoginSteps {

    private final TestContext context;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

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
                context.setErrorMessage(e.getMessage());
                throw e;
            }
    }
}
