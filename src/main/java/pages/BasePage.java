package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;

public class BasePage {

    public static WebDriver driver;

    public void setDriver(WebDriver driver) {
        BasePage.driver = driver;
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected WebElement waitForElementToBeClickable(By locator, int timeoutInSecond) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSecond));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForElementToBeVisible(By locator, int timeoutInSecond) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSecond));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void set(By locator, String text) {
        find(locator).clear();
        find(locator).sendKeys(text);
    }

    protected void click(By locator){
        find(locator).click();
    }
}
