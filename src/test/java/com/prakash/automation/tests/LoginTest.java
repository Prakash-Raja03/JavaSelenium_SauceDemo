package com.prakash.automation.tests;

import com.prakash.automation.base.BaseTest;
import com.prakash.automation.pages.LoginPage;
import com.prakash.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Login test suite: positive, negative, and data-driven scenarios.
 * Demonstrates TestNG @Test, priorities, groups, and @DataProvider.
 */
public class LoginTest extends BaseTest {

    @Test(priority = 1, groups = {"smoke"},
          description = "Valid credentials should log the user in and show Products page")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver).open();
        ProductsPage productsPage = loginPage.loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(productsPage.isLoaded(),
                "Products page should be displayed after a valid login");
        Assert.assertTrue(productsPage.getProductCount() > 0,
                "There should be at least one product listed");
    }

    @Test(priority = 2, groups = {"regression"},
          description = "Locked-out user should see the correct error message")
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.loginAs("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "An error message should appear");
        Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains("locked out"),
                "Error should mention the user is locked out");
    }

    /**
     * Data-driven negative tests. Each row runs as a separate test execution.
     */
    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"invalid_user", "secret_sauce", "Username and password do not match"},
                {"standard_user", "wrong_password", "Username and password do not match"},
                {"", "secret_sauce", "Username is required"},
                {"standard_user", "", "Password is required"}
        };
    }

    @Test(priority = 3, groups = {"regression"}, dataProvider = "invalidCredentials",
          description = "Invalid credential combinations should show the expected error")
    public void testInvalidLogin(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.loginAs(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "An error message should appear");
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedError),
                "Expected error to contain: '" + expectedError
                        + "' but was: '" + loginPage.getErrorMessage() + "'");
    }
}
