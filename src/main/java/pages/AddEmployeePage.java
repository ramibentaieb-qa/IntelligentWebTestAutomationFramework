package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddEmployeePage extends BasePage {

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    private By firstNameField = By.cssSelector("input.orangehrm-firstname");
    private By middleNameField = By.cssSelector("input.orangehrm-middlename");
    private By lastNameField = By.cssSelector("input.orangehrm-lastname");
    private By EmployeeIdField = By.xpath("//label[text()='Employee Id']/following::input[1]");
    private By checkboxButton = By.cssSelector("span.oxd-switch-input");
    private By usernameField = By.xpath("//div[contains(@class,'oxd-input-group')][.//label[text()='Username']]//input");
    private By enableRadioButton = By.xpath("//label[contains(.,'Enabled')]");
    private By disableRadioButton = By.xpath("//label[contains(.,'Disabled')]");
    private By passwordField = By.xpath("//label[text()='Password']/following::input[1]");
    private By confirmPasswordField = By.xpath("//label[text()='Confirm Password']/following::input[1]");
    private By cancelButton = By.cssSelector("button[type='button'].oxd-button");
    private By saveButton = By.cssSelector("button[type='submit'].oxd-button");
    private By errorMessage = By.cssSelector("span[class='oxd-text oxd-text--span oxd-input-field-error-message oxd-input-group__message']");
    private By successAlert = By.xpath("//div[contains(@class,'oxd-toast-content')]//p");



    public void enterFirstName(String firstName) {
        set(firstNameField, firstName);
    }

    public void enterMiddleName(String middleName) {
        set(middleNameField, middleName);
    }

    public void enterLastName(String lastName) {
        set(lastNameField, lastName);
    }

    public void enterEmployeeId(String employeeId) {
        set(EmployeeIdField, employeeId);
    }

    public void clickCheckboxToggleButton() {
        click(checkboxButton);
    }

    public void enterUsername(String username) {
        set(usernameField, username);
    }

    public void selectEnableRadio() {
        waitForElementToBeVisible(enableRadioButton, 10);
        click(enableRadioButton);
    }

    public void selectDisableRadio() {
        click(disableRadioButton);
    }

    public void enterPassword(String password) {
        set(passwordField, password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        set(confirmPasswordField, confirmPassword);
    }

    public void clickSaveButton() {
        click(saveButton);
    }

    public void clickCancelButton() {
        click(cancelButton);
    }

    public boolean getErrorMessage() {
        return find(errorMessage).isDisplayed();
    }

    public String getSuccessAlertMessage() {
        waitForElementToBeVisible(successAlert, 10);
        return find(successAlert).getText();
    }

}
