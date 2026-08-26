package com.prakash.automation.pages;

import com.prakash.automation.base.BasePage;
import com.prakash.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the SauceDemo login page (https://www.saucedemo.com).
 *
 * POM principle: locators are PRIVATE, actions are PUBLIC methods.
 * If a locator changes, we fix it here only — every test keeps working.
 */
public class LoginPage extends BasePage {

    // ---------- Locators ----------
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.id("login-button");
    private final By errorMessage  = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Actions ----------

    public LoginPage open() {
        driver.get(ConfigReader.get("baseUrl"));
        return this;
    }

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    /** Full login flow. Returns the ProductsPage (fluent page transition). */
    public ProductsPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new ProductsPage(driver);
    }

    // ---------- Validations ----------

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
