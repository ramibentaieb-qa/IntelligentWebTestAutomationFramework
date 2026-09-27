package pages;

import org.openqa.selenium.By;

public class DashboardPage extends BasePage{

    private By dashboard_title = By.tagName("h6");


    public String getDashboardTitle() {
        return find(dashboard_title).getText();
    }

}
