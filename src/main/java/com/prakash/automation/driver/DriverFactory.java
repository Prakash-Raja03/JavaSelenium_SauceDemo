package com.prakash.automation.driver;

import com.prakash.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Creates and manages the WebDriver instance.
 *
 * Key design points (great talking points in an interview):
 *  - ThreadLocal<WebDriver> makes the driver thread-safe so TestNG can run
 *    tests in PARALLEL without threads sharing/clobbering one browser.
 *  - Cross-browser support via a simple factory switch (polymorphism:
 *    all return the WebDriver interface type).
 *  - Selenium 4.6+ "Selenium Manager" auto-downloads driver binaries, so we
 *    normally do not need to manage chromedriver/geckodriver manually.
 */
public final class DriverFactory {

    // One WebDriver per thread -> safe parallel execution.
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initDriver() {
        if (DRIVER.get() == null) {
            String browser = ConfigReader.get("browser", "chrome").toLowerCase();
            boolean headless = ConfigReader.getBoolean("headless");

            WebDriver driver;
            switch (browser) {
                case "firefox":
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    if (headless) {
                        ffOptions.addArguments("-headless");
                    }
                    driver = new FirefoxDriver(ffOptions);
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (headless) {
                        edgeOptions.addArguments("--headless=new");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }
                    // Common CI-friendly flags
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            // Modest implicit wait as a safety net; real synchronization uses explicit waits.
            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait")));
            driver.manage().window().maximize();

            DRIVER.set(driver);
        }
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();   // closes all windows + ends session
            DRIVER.remove();       // clean up the ThreadLocal to avoid leaks
        }
    }
}
