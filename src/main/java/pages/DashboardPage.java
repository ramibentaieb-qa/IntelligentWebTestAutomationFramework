package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage{

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    private By dashboard_title = By.xpath("//h6[text()='Dashboard']");
    private By adminButton = By.xpath("//span[text()='Admin']/ancestor::a");
    private By PimButton  = By.xpath("//span[text()='PIM']/ancestor::a");


    public String getDashboardTitle() {
        waitForElementToBeVisible(dashboard_title, 10);
        return find(dashboard_title).getText();
    }

    public AdminUserPage clickAdminButton() {
        click(adminButton);
        return new AdminUserPage(driver);
    }

    public PimPage clickPimButton() {
        click(PimButton);
        return new PimPage(driver);
    }

}
