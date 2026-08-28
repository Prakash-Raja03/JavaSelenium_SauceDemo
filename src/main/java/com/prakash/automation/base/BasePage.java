package com.prakash.automation.base;

import com.prakash.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BasePage centralises common WebDriver interactions with proper
 * synchronization (explicit waits). Every Page Object extends this,
 * so pages stay clean and we never repeat wait/click boilerplate.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Incrementing counter so screenshot files sort in execution order.
    private static final AtomicInteger STEP = new AtomicInteger(0);

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWait")));
    }

    /**
     * When 'screenshotSteps' is true, saves a PNG of the current page into the
     * 'screenshots/' folder so the full workflow can be reviewed step by step.
     * No-op (and fully swallowed) otherwise, so it can NEVER fail a test.
     */
    protected void capture(String label) {
        if (!ConfigReader.getBoolean("screenshotSteps")) {
            return;
        }
        try {
            Path dir = Paths.get("screenshots");
            Files.createDirectories(dir);
            String stamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HHmmss"));
            String safe = label.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = String.format("%04d_%s_%s.png",
                    STEP.incrementAndGet(), stamp, safe);
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(fileName));
        } catch (Exception e) {
            // Purely a debugging aid — never let screenshot problems break a test.
            System.out.println("[capture] skipped screenshot for '" + label
                    + "': " + e.getMessage());
        }
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
        capture("click");
    }

    protected void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
        capture("type");
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
        capture("select_" + visibleText);
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitForVisible(locator)).selectByValue(value);
        capture("select_" + value);
    }

    // ---------- Actions (mouse/keyboard) ----------

    protected void hover(By locator) {
        new Actions(driver).moveToElement(waitForVisible(locator)).perform();
        capture("hover");
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
