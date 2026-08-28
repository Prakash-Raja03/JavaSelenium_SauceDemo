# Selenium TestNG Automation Framework

A production-style UI test automation framework built with **Java + Selenium WebDriver 4 + TestNG**, following the **Page Object Model (POM)** design pattern.

The framework automates the [SauceDemo](https://www.saucedemo.com) e-commerce demo application and demonstrates the core building blocks used in real-world Selenium frameworks.

---

## Highlights

- **Page Object Model (POM)** — clean separation of locators (private) and actions (public).
- **Cross-browser** — Chrome, Firefox, Edge via a simple `DriverFactory`.
- **Thread-safe parallel execution** — `ThreadLocal<WebDriver>` + TestNG `parallel` suites.
- **Explicit waits everywhere** — robust synchronization, zero `Thread.sleep()`.
- **Data-driven testing** — TestNG `@DataProvider`.
- **Hard & soft assertions** — `Assert` and `SoftAssert`.
- **Externalised config** — `config.properties`, overridable via `-D` system properties.
- **Selenium Manager** — automatic driver-binary management (no manual chromedriver).
- **CI-ready** — headless mode + Maven Surefire; drop straight into Jenkins/GitHub Actions.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Automation | Selenium WebDriver 4.25 |
| Test runner | TestNG 7.10 |
| Build tool | Maven |
| Design pattern | Page Object Model |
| Driver mgmt | Selenium Manager (WebDriverManager as fallback) |

---

## Project Structure

```
selenium-testng-framework/
├── pom.xml
├── README.md
├── .gitignore
└── src
    ├── main/java/com/prakash/automation/
    │   ├── config/    ConfigReader.java        # reads config.properties + -D overrides
    │   ├── driver/    DriverFactory.java        # ThreadLocal WebDriver, cross-browser
    │   ├── base/      BasePage.java             # waited wrappers: click/type/select/JS
    │   └── pages/     LoginPage.java            # Page Objects (locators + actions)
    │                  ProductsPage.java
    └── test
        ├── java/com/prakash/automation/
        │   ├── base/  BaseTest.java             # driver setup/teardown lifecycle
        │   └── tests/ LoginTest.java            # positive/negative + data-driven
        │               ProductsTest.java         # sorting, cart, soft assertions
        └── resources/
            ├── config/config.properties
            └── suites/testng.xml                # full suite (parallel)
                       smoke.xml                  # smoke group only
```

---

## Prerequisites

- **Java 17+** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Google Chrome** (default browser; Firefox/Edge also supported)

---

## How to Run

Run all tests (headless, parallel):
```bash
mvn test
```

Run only the **smoke** suite:
```bash
mvn test -DsuiteXmlFile=src/test/resources/suites/smoke.xml
```

Run in a **visible** browser (watch it drive):
```bash
mvn test -Dheadless=false
```

Run on **Firefox** or **Edge**:
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

Override the app URL / waits at runtime:
```bash
mvn test -DbaseUrl=https://www.saucedemo.com -DexplicitWait=20
```

---

## What the Tests Cover

**LoginTest**
- Valid login lands on the Products page.
- Locked-out user shows the correct error.
- Data-driven invalid-credential scenarios (4 combinations) via `@DataProvider`.

**ProductsTest**
- Exactly 6 products are listed.
- Sorting by "Price (low to high)" produces ascending order.
- Adding items updates the cart badge.
- Multiple validations in one test using `SoftAssert`.

Verified run: **10 tests, 0 failures** (headless Chrome).

---

## Extending the Framework

- Add a new page → create a class under `pages/` extending `BasePage`.
- Add a new test → create a class under `tests/` extending `BaseTest`.
- Add Extent/Allure reporting, screenshots-on-failure (TestNG `ITestListener`), and a GitHub Actions workflow for full CI.

