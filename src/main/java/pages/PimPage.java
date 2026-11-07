package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PimPage extends BasePage{

    public PimPage(WebDriver driver) {
        super(driver);
    }

    private By title = By.cssSelector("h6.oxd-topbar-header-breadcrumb-module");
    private By addButton = By.cssSelector("div.orangehrm-header-container > button");


    public String getPageTitle() {
        return find(title).getText();
    }

    public AddEmployeePage navigateToAddEmployeePage() {
        click(addButton);
        return new AddEmployeePage(driver);
    }


}
