package com.prakash.automation.base;

import com.prakash.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest owns the driver lifecycle so individual test classes don't repeat it.
 *
 * TestNG lifecycle used here:
 *   @BeforeMethod -> runs before EACH @Test  (fresh browser per test = isolation)
 *   @AfterMethod  -> runs after EACH @Test   (always quits -> no leaked browsers)
 *
 * Inheritance in action: every test class 'extends BaseTest'.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
