package stepdefinitions;

import base.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class UserSteps {

    private final TestContext context;

    public UserSteps(TestContext context) {
        this.context = context;
    }


    @Given("the admin is logged in")
    public void AdminLoggedIn(String username, String password) {
        context.getDriver().get(TestContext.url);
        context.loginPage().enterPassword(username);
        context.loginPage().enterPassword(password);
        context.loginPage().clickOnButtonLogin();
    }

    @When("he open the Add User form")
    public void navigateToAddUserPage() {
        context.dashboardPage().clickAdminButton();
        context.adminUserPage().clickAddButton();
    }

    @And("selects user role {string}")
    public void selectUserRole(String role) {
        context.addUserPage().selectUserRole(role);
    }

    @And("selects status {string}")
    public void selectUserStatus(String status) {
        context.addUserPage().selectStatus(status);
    }

    @And("enters employee name {string}")
    public void enterEmployeeName(String employeeName) {
        context.addUserPage().enterEmployeeName(employeeName);
    }

    @And("enters his username {string}")
    public void enterUsername(String username) {
        context.addUserPage().enterUsername(username);
    }

    @And("enters his password {string}")
    public void enterPassword(String password) {
        context.addUserPage().enterPassword(password);
    }

    @And("confirms his the password {string}")
    public void enterConfirmPassword(String confirmPassword) {
        context.addUserPage().enterConfirmPassword(confirmPassword);
    }

    @And("clicks on the save button")
    public void clickOnSaveButton() {
        context.addUserPage().clickSave();
    }

    @Then("the new user should be added {string}")
    public void verifyAdminPage(String expectedResult) {
        try {
            if (expectedResult.contains("User Management")) {
                String actualResult = context.adminUserPage().getTitle();
                Assertions.assertEquals(expectedResult, actualResult);
            } else {
                boolean actualError = context.addUserPage().getErrorMessage();
                Assertions.assertTrue(actualError,  expectedResult);
            }
        }catch (AssertionError | RuntimeException e) {
            context.setErrorMessage(e.getMessage());
            throw e;
        }
    }













}
