package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddUserPage extends BasePage {

    public AddUserPage(WebDriver driver) {
        super(driver);
    }


    private By userRoleDropdown = By.xpath("//label[text()='User Role']/../following-sibling::div//div[contains(@class,'oxd-select-text-input')]");
    private By statusDropdown = By.xpath("//label[text()='Status']/../following-sibling::div//div[contains(@class,'oxd-select-text-input')]");
    private By employeeNameField = By.cssSelector("input[placeholder='Type for hints...']");
    private By usernameField = By.xpath("//label[text()='Username']/../following-sibling::div//input[contains(@class,'oxd-input')]");
    private By passwordField = By.xpath("//label[text()='Password']/../following-sibling::div//input[contains(@class,'oxd-input')]");
    private By confirmPasswordField = By.xpath("//label[text()='Confirm Password']/../following-sibling::div//input[contains(@class,'oxd-input')]");
    private By errorMessage = By.cssSelector("span[class='oxd-text oxd-text--span oxd-input-field-error-message oxd-input-group__message']");
    private By cancelButton = By.cssSelector("button[type='button'].oxd-button");
    private By saveButton = By.cssSelector("button[type='submit'].oxd-button");

    private By dropdownOptions = By.cssSelector(".oxd-select-option");
    private By employeeSuggestions = By.cssSelector(".oxd-autocomplete-option");


    public void selectUserRole(String role) {
        click(userRoleDropdown);
        waitForElementToBeVisible(dropdownOptions, 5);
        driver.findElement(By.xpath(
                "//div[@class='oxd-select-option']//span[text()='" + role + "']"
        )).click();
    }


    public void selectStatus(String status) {
        click(statusDropdown);
        waitForElementToBeVisible(dropdownOptions, 5);
        driver.findElement(By.xpath(
                "//div[@class='oxd-select-option']//span[text()='" + status + "']"
        )).click();
    }

    public void enterEmployeeName(String employeeName) {
        String[] parts = employeeName.trim().split("\\s+");
        String first = parts[0];
        String last = parts[parts.length - 1];

        set(employeeNameField, employeeName);

        By suggestion = By.xpath("//div[contains(@class,'oxd-autocomplete-option')]"
         + "//span[contains(.,'" + first + "') and contains(.,'" + last + "')]");
        waitForElementToBeClickable(suggestion, 10).click();
    }


    public void enterUsername(String username) {
        set(usernameField, username);
    }

    public void enterPassword(String password) {
        set(passwordField, password);
    }

    public void enterConfirmPassword(String password) {
        set(confirmPasswordField, password);
    }

    public Boolean getErrorMessage() {
        return find(errorMessage).isDisplayed();
    }

    public void clickSave() {
        waitForElementToBeVisible(saveButton, 10).click();
    }

    public void clickCancel() {
        click(cancelButton);
    }


}
