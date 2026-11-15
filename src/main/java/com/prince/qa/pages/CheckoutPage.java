package com.prince.qa.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By finishButton = By.id("finish");
    private final By errorContainer = By.cssSelector("[data-test='error']");
    private final By subtotalLabel = By.cssSelector(".summary_subtotal_label");
    private final By taxLabel = By.cssSelector(".summary_tax_label");
    private final By totalLabel = By.cssSelector(".summary_total_label");
    private final By orderConfirmation = By.cssSelector(".complete-header");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void type(By locator, String value) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        el.clear();
        if (value != null && !value.isEmpty()) {
            el.sendKeys(value);
            if (!value.equals(el.getAttribute("value"))) {
                el.clear();
                el.sendKeys(value);
            }
        }
    }

    public CheckoutPage enterCustomerDetails(String first, String last, String postal) {
        fillCustomer(first, last, postal);
        continueCheckout();
        return this;
    }

    public void fillCustomer(String first, String last, String postal) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, postal);
    }

    public CheckoutPage continueCheckout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
        return this;
    }

    public ProductsPage cancelCheckout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
        return new ProductsPage(driver);
    }

    public CheckoutPage finishOrder() {
        finish();
        return this;
    }

    public void finish() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(finishButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
    }

    public String getConfirmationMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderConfirmation)).getText();
    }

    public boolean isOrderConfirmationDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderConfirmation)).isDisplayed();
    }

    public String getErrorMessage() {
        return errorText();
    }

    public String errorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorContainer)).getText();
    }

    public String subtotalText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtotalLabel)).getText();
    }

    public String taxText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(taxLabel)).getText();
    }

    public String totalText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(totalLabel)).getText();
    }
}
