package com.prince.qa.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl);
        return this;
    }

    public ProductsPage loginAs(String user, String pass) {
        login(user, pass);
        return new ProductsPage(driver);
    }

    public LoginPage loginExpectingFailure(String user, String pass) {
        login(user, pass);
        return this;
    }

    public void login(String user, String pass) {
        WebElement userEl = wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        userEl.clear();
        if (user != null && !user.isEmpty()) {
            userEl.sendKeys(user);
        }

        WebElement passEl = wait.until(ExpectedConditions.visibilityOfElementLocated(password));
        passEl.clear();
        if (pass != null && !pass.isEmpty()) {
            passEl.sendKeys(pass);
        }

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        btn.click();
    }

    public String getErrorMessage() {
        return errorText();
    }

    public String errorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isLoginButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
