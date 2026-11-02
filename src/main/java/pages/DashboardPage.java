package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage{

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    private By dashboard_title = By.tagName("h6");
    private By adminButton = By.xpath("ul > li:nth-child(1) > a");


    public String getDashboardTitle() {
        return find(dashboard_title).getText();
    }

    public AdminUserPage clickAdminButton() {
        click(adminButton);
        return new AdminUserPage(driver);
    }

}
