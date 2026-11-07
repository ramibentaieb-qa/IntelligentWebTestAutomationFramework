package base;

import ai.FailureContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.*;

import java.time.Duration;

public class TestContext {

    public static final String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private AdminUserPage adminUserPage;
    private AddUserPage addUserPage;
    private PimPage pimPage;
    private AddEmployeePage addEmployeePage;

    private String errorMessage;

    public static final String ADMIN_USERNAME = "Admin";
    public static final String ADMIN_PASSWORD = "admin123";


    public void initDriver() {
        ChromeOptions options = new ChromeOptions();
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--headless=new", "--no-sandbox",
                    "--disable-dev-shm-usage", "--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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

    public PimPage pimPage() {
        if (pimPage == null) pimPage = new PimPage(driver);
        return pimPage;
    }

    public AddEmployeePage addEmployeePage() {
        if (addEmployeePage == null) addEmployeePage = new AddEmployeePage(driver);
        return addEmployeePage;
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
