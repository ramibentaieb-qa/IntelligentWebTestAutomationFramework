package stepdefinitions;

import base.TestContext;
import base.TestData;
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
    public void AdminLoggedIn() {
        context.getDriver().get(TestContext.url);
        context.loginPage().enterUsername(TestContext.ADMIN_USERNAME);
        context.loginPage().enterPassword(TestContext.ADMIN_PASSWORD);
        context.loginPage().clickOnButtonLogin();
        context.dashboardPage().getDashboardTitle();
    }

    @And("an employee exists")
    public void anEmployeeExists() {
        String firstName = "Rami";
        String lastName = "Tester" + TestData.uniqueLetters();

        context.dashboardPage().clickPimButton();
        context.pimPage().getPageTitle();
        context.pimPage().navigateToAddEmployeePage();

        context.addEmployeePage().enterFirstName(firstName);
        context.addEmployeePage().enterLastName(lastName);
        context.addEmployeePage().clickSaveButton();
        context.addEmployeePage().getSuccessAlertMessage();

        context.setEmployeeFirstName(firstName);
        context.setEmployeeLastName(lastName);
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
        String fullName = context.getEmployeeFirstName() + " " + context.getEmployeeLastName();
        context.addUserPage().enterEmployeeName(fullName);
    }

    @And("enters his username {string}")
    public void enterUsername(String username) {
        context.addUserPage().enterUsername(username + TestData.uniqueNumber());
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
