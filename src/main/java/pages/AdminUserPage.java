package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdminUserPage extends BasePage {

    public AdminUserPage(WebDriver driver) {
        super(driver);
    }

    private By title = By.cssSelector("h6.oxd-topbar-header-breadcrumb-level");
    private By addButton = By.cssSelector("div.orangehrm-header-container > button");


    public String getTitle() {
        return waitForElementToBeVisible(title, 15).getText();
    }

    public AddUserPage clickAddButton() {
        click(addButton);
        return new AddUserPage(driver);
    }





}
