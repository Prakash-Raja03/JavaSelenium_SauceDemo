package com.prakash.automation.tests;

import com.prakash.automation.base.BaseTest;
import com.prakash.automation.pages.LoginPage;
import com.prakash.automation.pages.ProductsPage;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Products page tests: counting, sorting, cart, and logout.
 * Demonstrates list handling, sorting verification, and SoftAssert.
 */
public class ProductsTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = {})
    public void loginFirst() {
        // setUp() in BaseTest runs first (creates driver), then this logs in.
        productsPage = new LoginPage(driver).open()
                .loginAs("standard_user", "secret_sauce");
    }

    @Test(groups = {"smoke"}, description = "SauceDemo lists exactly 6 products")
    public void testProductCount() {
        Assert.assertEquals(productsPage.getProductCount(), 6,
                "SauceDemo should list exactly 6 products");
    }

    @Test(groups = {"regression"}, description = "Sorting by price low-to-high orders ascending")
    public void testSortByPriceLowToHigh() {
        productsPage.sortBy("Price (low to high)");

        List<Double> actual = productsPage.getProductPrices();
        List<Double> expected = new ArrayList<>(actual);
        expected.sort(Double::compareTo);

        Assert.assertEquals(actual, expected,
                "Prices should be sorted in ascending order");
    }

    @Test(groups = {"regression"}, description = "Adding items updates the cart badge count")
    public void testAddToCartUpdatesBadge() {
        productsPage.addProductsToCart(3);
        Assert.assertEquals(productsPage.getCartCount(), 3,
                "Cart badge should show 3 after adding 3 products");
    }

    @Test(groups = {"regression"}, description = "Multiple product-page validations with SoftAssert")
    public void testProductPageWithSoftAssertions() {
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(productsPage.isLoaded(), "Products page should be loaded");
        soft.assertEquals(productsPage.getProductCount(), 6, "Should have 6 products");
        soft.assertTrue(productsPage.getProductNames().contains("Sauce Labs Backpack"),
                "Backpack should be present");
        soft.assertTrue(productsPage.getCartCount() == 0, "Cart should start empty");

        soft.assertAll();   // required: reports all collected failures at once
    }
}
