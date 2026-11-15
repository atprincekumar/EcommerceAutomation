package com.prince.qa.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartTitle = By.cssSelector(".title");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By itemName = By.cssSelector(".cart_item .inventory_item_name");
    private final By itemRemoveButtons = By.cssSelector(".cart_item button");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By firstNameInput = By.id("first-name");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CartPage open(String baseUrl) {
        driver.get(baseUrl + "/cart.html");
        return this;
    }

    public boolean isCartPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.textToBe(cartTitle, "Your Cart"));
        } catch (Exception e) {
            return false;
        }
    }

    public int getItemCount() {
        return itemCount();
    }

    public int itemCount() {
        return driver.findElements(cartItems).size();
    }

    public boolean isEmpty() {
        return driver.findElements(cartItems).isEmpty();
    }

    public List<String> getItemNames() {
        return itemNames();
    }

    public List<String> itemNames() {
        wait.until(d -> !d.findElements(cartItems).isEmpty());
        return driver.findElements(itemName)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    public CartPage removeFirstItem() {
        wait.until(d -> !d.findElements(cartItems).isEmpty());
        List<WebElement> removeButtons = driver.findElements(itemRemoveButtons);
        if (removeButtons.isEmpty()) {
            throw new IllegalStateException("Cannot remove item: no remove button found in cart.");
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", removeButtons.get(0));
        return this;
    }

    public CheckoutPage proceedToCheckout() {
        return checkout();
    }

    public CheckoutPage checkout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        return new CheckoutPage(driver);
    }

    public ProductsPage continueShopping() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
        return new ProductsPage(driver);
    }
}
