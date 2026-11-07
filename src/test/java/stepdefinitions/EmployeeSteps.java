package stepdefinitions;

import base.TestContext;
import base.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class EmployeeSteps {

    private final TestContext context;

    public EmployeeSteps(TestContext context) {
        this.context = context;
    }

    @Given("the admin is logged in the OHR")
    public void AdminLoggedIn() {
        context.getDriver().get(TestContext.url);
        context.loginPage().enterUsername(TestContext.ADMIN_USERNAME);
        context.loginPage().enterPassword(TestContext.ADMIN_PASSWORD);
        context.loginPage().clickOnButtonLogin();
        context.dashboardPage().getDashboardTitle();
    }

    @When("he opens the Add Employee form")
    public void navigateToAddEmployeePage() {
        context.dashboardPage().clickPimButton();
        context.pimPage().getPageTitle();
        context.pimPage().navigateToAddEmployeePage();
    }

    @And("types employee's full name {string}, {string}, and {string}")
    public void enterEmployeeFullName(String firstName, String middleName, String lastName) {
        String uniqueLastName = lastName + TestData.uniqueLetters();
        context.setEmployeeFirstName(firstName);
        context.setEmployeeLastName(uniqueLastName);

        context.addEmployeePage().enterFirstName(firstName);
        context.addEmployeePage().enterMiddleName(middleName);
        context.addEmployeePage().enterLastName(uniqueLastName);
    }

    @And("types a unique employee id")
    public void enterEmployeeId() {
        context.addEmployeePage().enterEmployeeId(TestData.uniqueNumber());
    }

    @And("opens the login details")
    public void openLoginDetails() {
        context.addEmployeePage().clickCheckboxToggleButton();
    }

    @And("enters employee's username {string}")
    public void enterUsername(String username) {
        context.addEmployeePage().enterUsername(username + TestData.uniqueNumber());
    }

    @And("chooses the status of the employee")
    public void chooseEmployeeStatus() {
        context.addEmployeePage().selectEnableRadio();
    }

    @And("enters employee's password {string}")
    public void enterEmployeePassword(String password) {
        context.addEmployeePage().enterPassword(password);
    }

    @And("confirms employee's password {string}")
    public void confirmEmployeePassword(String password) {
        context.addEmployeePage().enterConfirmPassword(password);
    }

    @And("clicks on the save button to add the new employee")
    public void saveEmployee() {
        context.addEmployeePage().clickSaveButton();
    }

    @Then("the new employee should be added {string}")
    public void verifyAddedEmployee(String expectedResult) {
        try {
            if (expectedResult.contains("Success")) {
                String actualResult = context.addEmployeePage().getSuccessAlertMessage();
                Assertions.assertEquals(actualResult, expectedResult);
            } else {
                boolean ActualError = context.addEmployeePage().getErrorMessage();
                Assertions.assertTrue(ActualError, expectedResult);
            }
        } catch (AssertionError | RuntimeException e) {
            context.setErrorMessage(e.getMessage());
            throw e;
        }
    }

}
