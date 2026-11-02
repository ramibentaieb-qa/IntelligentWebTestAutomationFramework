package base;

import ai.FailureContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.AddUserPage;
import pages.AdminUserPage;
import pages.DashboardPage;
import pages.LoginPage;

import java.time.Duration;

public class TestContext {

    public static final String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private AdminUserPage adminUserPage;
    private AddUserPage addUserPage;

    private String errorMessage;

    public static final String ADMIN_USERNAME = "Admin";
    public static final String ADMIN_PASSWORD = "admin123";


    public void initDriver() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new", "--no-sandbox",
//                "--disable-dev-shm-usage", "--window-size=1920,1080");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
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

    public AdminUserPage adminUserPage() {
        if (adminUserPage == null) adminUserPage = new AdminUserPage(driver);
        return adminUserPage;
    }

    public AddUserPage addUserPage() {
        if (addUserPage == null) addUserPage = new AddUserPage(driver);
        return addUserPage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
