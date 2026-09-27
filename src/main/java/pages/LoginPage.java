package pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {


    private By username_input = By.name("username");
    private By password_input = By.name("password");
    private By login_button = By.tagName("button");
    private By error_message = By.xpath("//p[text()='Invalid credentials']");


    //Constructors

    public void enterUsername(String username) {
        set(username_input, username);
    }

    public void enterPassword(String password) {
        set(password_input, password);
    }

    public DashboardPage clickOnButtonLogin() {
        click(login_button);
        return new DashboardPage();
    }

    public String getErrorMessage() {
        return find(error_message).getText();
    }




















}
