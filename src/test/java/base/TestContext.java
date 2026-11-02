package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.DashboardPage;
import pages.LoginPage;

public class TestContext {

    public static final String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;


    public void initDriver() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public LoginPage loginPage() {
        if (loginPage == null) loginPage = new LoginPage(driver);
        return loginPage;
    }

    public DashboardPage dashboardPage() {
        if (dashboardPage == null) dashboardPage = new DashboardPage(driver);
        return dashboardPage;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
