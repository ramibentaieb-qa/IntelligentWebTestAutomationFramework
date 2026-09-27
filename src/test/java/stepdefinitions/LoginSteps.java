package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.BasePage;
import pages.DashboardPage;
import pages.LoginPage;

import java.time.Duration;


public class LoginSteps {

    protected WebDriver driver;
    protected BasePage basePage;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;


    private String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";


    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--diasbla-dev-shm-usage");
        driver = new ChromeDriver(options);

//        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        basePage = new BasePage();
        basePage.setDriver(driver);
        loginPage = new LoginPage();
    }
    @Given("the user is on the login page")
    public void navigateToLoginPage() {
        driver.get(url);
    }

    @When("he enters his {string} and his {string}")
    public void enterCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @And("clicks on the login button")
    public void submitLogin() {
        this.dashboardPage = loginPage.clickOnButtonLogin();
    }

    @Then("he should be redirected to the {string} page")
    public void verifyDashboardPage(String expectedResult) {
        if (expectedResult.contains("Dashboard")) {
            String actualResult = dashboardPage.getDashboardTitle();
            Assert.assertEquals(expectedResult, actualResult);
        } else {
            String actualError = loginPage.getErrorMessage();
            Assert.assertEquals(expectedResult, actualError);
        }
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
