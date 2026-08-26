package com.prakash.automation.pages;

import com.prakash.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Products (inventory) page shown after a successful login.
 * Demonstrates: element lists, counting, dropdown (Select), and reading text.
 */
public class ProductsPage extends BasePage {

    // ---------- Locators ----------
    private final By title            = By.className("title");
    private final By inventoryItems   = By.className("inventory_item");
    private final By itemNames        = By.className("inventory_item_name");
    private final By itemPrices       = By.className("inventory_item_price");
    private final By sortDropdown     = By.className("product_sort_container");
    private final By cartBadge        = By.className("shopping_cart_badge");
    private final By addToCartButtons = By.cssSelector("button[id^='add-to-cart']");
    private final By menuButton       = By.id("react-burger-menu-btn");
    private final By logoutLink       = By.id("logout_sidebar_link");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Queries ----------

    public boolean isLoaded() {
        return isDisplayed(title) && getText(title).equalsIgnoreCase("Products");
    }

    public int getProductCount() {
        return count(inventoryItems);
    }

    public List<String> getProductNames() {
        return findElements(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getProductPrices() {
        return findElements(itemPrices).stream()
                .map(e -> e.getText().replace("$", "").trim())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    // ---------- Actions ----------

    public void sortBy(String visibleText) {
        selectByVisibleText(sortDropdown, visibleText);
    }

    /** Adds the first N products to the cart. */
    public void addProductsToCart(int n) {
        List<WebElement> buttons = findElements(addToCartButtons);
        int limit = Math.min(n, buttons.size());
        for (int i = 0; i < limit; i++) {
            buttons.get(i).click();
        }
    }

    public int getCartCount() {
        List<WebElement> badge = findElements(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    public LoginPage logout() {
        click(menuButton);
        click(logoutLink);
        return new LoginPage(driver);
    }
}
