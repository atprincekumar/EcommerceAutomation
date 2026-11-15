package com.prince.qa.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitle = By.cssSelector(".title");
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By sortDropdown = By.cssSelector(".product_sort_container");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isProductsPageDisplayed() {
        return isDisplayed();
    }

    public boolean isDisplayed() {
        return wait.until(d -> !d.findElements(inventoryItems).isEmpty()
                || d.findElement(pageTitle).isDisplayed());
    }

    public void sortBy(String optionValue) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(sortDropdown));
        new Select(dropdown).selectByValue(optionValue);
    }

    public List<BigDecimal> getPrices() {
        wait.until(d -> !d.findElements(inventoryItems).isEmpty());
        List<BigDecimal> prices = new ArrayList<>();
        for (WebElement item : driver.findElements(inventoryItems)) {
            String raw = item.findElement(By.cssSelector(".inventory_item_price")).getText();
            prices.add(new BigDecimal(raw.replace("$", "").trim()));
        }
        return prices;
    }

    public List<String> getNames() {
        wait.until(d -> !d.findElements(inventoryItems).isEmpty());
        List<String> names = new ArrayList<>();
        for (WebElement item : driver.findElements(inventoryItems)) {
            names.add(item.findElement(By.cssSelector(".inventory_item_name")).getText());
        }
        return names;
    }

    public ProductsPage addFirstProductToCart() {
        addFirstNItems(1);
        return this;
    }

    public void addFirstNItems(int count) {
        wait.until(d -> d.findElements(inventoryItems).size() >= count);
        for (int i = 0; i < count; i++) {
            int itemIndex = i;
            WebElement addButton = wait.until(d -> {
                List<WebElement> currentItems = d.findElements(inventoryItems);
                if (currentItems.size() <= itemIndex) return null;
                WebElement button = currentItems.get(itemIndex).findElement(By.cssSelector("button"));
                return button.isDisplayed() && button.isEnabled() ? button : null;
            });
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", addButton);
            wait.until(d -> {
                List<WebElement> currentItems = d.findElements(inventoryItems);
                return currentItems.size() > itemIndex
                        && "Remove".equalsIgnoreCase(currentItems.get(itemIndex)
                                .findElement(By.cssSelector("button")).getText().trim());
            });
        }
    }

    public ProductsPage removeFirstProductFromCart() {
        removeFirstItem();
        return this;
    }

    public void removeFirstItem() {
        WebElement removeButton = wait.until(d -> {
            List<WebElement> items = d.findElements(inventoryItems);
            if (items.isEmpty()) return null;
            WebElement button = items.get(0).findElement(By.cssSelector("button"));
            return button.isDisplayed() && button.isEnabled()
                    && "Remove".equalsIgnoreCase(button.getText().trim()) ? button : null;
        });
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", removeButton);
        wait.until(d -> {
            List<WebElement> items = d.findElements(inventoryItems);
            return !items.isEmpty()
                    && "Add to cart".equalsIgnoreCase(items.get(0)
                            .findElement(By.cssSelector("button")).getText().trim());
        });
    }

    public String getCartCount() {
        return cartBadgeCount();
    }

    public String cartBadgeCount() {
        List<WebElement> badges = driver.findElements(cartBadge);
        return badges.isEmpty() ? "0" : badges.get(0).getText().trim();
    }

    public boolean isCartBadgeDisplayed() {
        return !driver.findElements(cartBadge).isEmpty();
    }

    public CartPage openCart() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(cartLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", link);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", menu);

        WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(logoutLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", logout);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        return new LoginPage(driver);
    }

    public void openDirectUrl(String baseUrl) {
        driver.get(baseUrl + "/inventory.html");
    }
}
