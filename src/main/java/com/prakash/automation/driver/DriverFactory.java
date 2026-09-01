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
import java.util.UUID;

/**
 * Creates and manages the WebDriver instance.
 *
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

    // A unique --user-data-dir per Chrome session. On teardown we forcibly kill
    // any browser process whose command line contains this exact directory.
    // This guarantees no leftover (headless) Chrome processes even when the OS
    // re-parents them to systemd/init (where driver.quit() and PID-tree kills
    // fail), because we match on a marker string that only our session uses.
    private static final ThreadLocal<String> USER_DATA_DIR = new ThreadLocal<>();

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
                    // Unique profile dir per session; also used as a kill-marker on teardown.
                    String udd = System.getProperty("java.io.tmpdir")
                            + "/sel-udd-" + UUID.randomUUID();
                    USER_DATA_DIR.set(udd);
                    chromeOptions.addArguments("--user-data-dir=" + udd);
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
        WebDriver driver = DRIVER.get();
        String udd = USER_DATA_DIR.get();
        if (driver != null) {
            try {
                driver.quit();   // closes all windows + ends session
            } catch (Exception e) {
                // Browser/session may already be dead (e.g. crashed). Swallow so
                // teardown never leaks the ThreadLocal reference.
                System.err.println("quitDriver: ignoring error while quitting driver: " + e.getMessage());
            } finally {
                DRIVER.remove();  // always clean up the ThreadLocal to avoid leaks
            }
        }
        // Safety net: some containers re-parent the browser to systemd/init so it
        // survives quit(). Forcibly kill any process whose command line contains
        // this session's unique --user-data-dir marker.
        killByUserDataDir(udd);
        USER_DATA_DIR.remove();
    }

    /**
     * Forcibly kills any (Chrome) process launched with the given unique
     * --user-data-dir. Matching on this session-owned marker string is immune
     * to OS process re-parenting and to headless Chrome's detached processes.
     * Best-effort: only runs on Linux/macOS where pkill is available.
     */
    private static void killByUserDataDir(String udd) {
        if (udd == null || udd.isEmpty()) {
            return;
        }
        try {
            // -f matches against the full command line; the UUID makes it unique.
            new ProcessBuilder("pkill", "-9", "-f", udd)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor();
        } catch (Exception e) {
            System.err.println("killByUserDataDir: could not kill browser for " + udd + ": " + e.getMessage());
        }
    }
}
