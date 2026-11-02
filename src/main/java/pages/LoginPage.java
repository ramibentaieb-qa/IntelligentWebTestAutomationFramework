package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    private By username_input = By.name("username");
    private By password_input = By.name("password");
    private By login_button = By.tagName("button");
    private By error_message = By.xpath("//p[text()='Invalid credentials']");


    //Constructors

    public void enterUsername(String username) {
        waitForElementToBeVisible(username_input, 10);
        set(username_input, username);
    }

    public void enterPassword(String password) {
        waitForElementToBeVisible(password_input, 10);
        set(password_input, password);
    }

    public DashboardPage clickOnButtonLogin() {
        waitForElementToBeVisible(login_button, 10);
        click(login_button);
        return new DashboardPage(driver);
    }

    public boolean getErrorMessage() {
        return waitForElementToBeVisible(error_message, 10).isDisplayed();
    }




















}
