package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage{

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    private By dashboard_title = By.xpath("//h6[text()='Dashboard']");
    private By adminButton = By.xpath("ul > li:nth-child(1) > a");


    public String getDashboardTitle() {
        waitForElementToBeVisible(dashboard_title, 10);
        return find(dashboard_title).getText();
    }

    public AdminUserPage clickAdminButton() {
        click(adminButton);
        return new AdminUserPage(driver);
    }

}
