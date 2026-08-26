package com.prakash.automation.base;

import com.prakash.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage centralises common WebDriver interactions with proper
 * synchronization (explicit waits). Every Page Object extends this,
 * so pages stay clean and we never repeat wait/click boilerplate.
 *
 * This class is essentially a set of "safe" wrappers around raw Selenium
 * calls — a very common real-world framework pattern.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWait")));
    }

    // ---------- Core waited interactions ----------

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** findElements returns an empty list (not an exception) when nothing matches. */
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected int count(By locator) {
        return findElements(locator).size();
    }

    // ---------- Dropdowns (native <select>) ----------

    protected void selectByVisibleText(By locator, String visibleText) {
        new Select(waitForVisible(locator)).selectByVisibleText(visibleText);
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitForVisible(locator)).selectByValue(value);
    }

    // ---------- Actions (mouse/keyboard) ----------

    protected void hover(By locator) {
        new Actions(driver).moveToElement(waitForVisible(locator)).perform();
    }

    // ---------- JavaScriptExecutor helpers ----------

    protected void jsClick(By locator) {
        WebElement el = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    protected void scrollIntoView(By locator) {
        WebElement el = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }

    // ---------- Page info ----------

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
